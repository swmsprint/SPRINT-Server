package sprint.server.oauth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.member.Authority;
import sprint.server.domain.member.Member;
import sprint.server.oauth.dto.token.TokenDto;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    /* ---------------------- 토큰 발급 수정 필드 ------------------------- */
    @Value("${jwt.secret}")
    private String secretKey;
    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
    private Key key;
    private final RedisService redisService;
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30; //access 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7; //refresh 7일
    /* ---------------------- 토큰 발급 수정 메서드 ------------------------- */

    public TokenDto generateTokenDto(Member member) {

        long now = (new Date()).getTime();

        // Access Token 생성
        log.info("4-2-1. Access Token 생성");
        Date date = new Date();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(String.valueOf(member.getId())) //payload "sub" : "name"
                .claim(AUTHORITIES_KEY, Authority.ROLE_USER) //payload "auth" : "ROLE_USER"
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRE_TIME)) //payload "exp" : 1234567890 (10자리)
                .signWith(key, SignatureAlgorithm.HS512) //header "alg" : HS512 (해싱 알고리즘 HS512)
                .compact();

        // Refresh Token 생성
        // refresh~에는 claim 없이 만료시간만 담아줌
        log.info("4-2-2. Refresh Token 생성");
        String refreshToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        log.info("4-2-3. Refresh Token redis 저장");
        redisService.setValues(Long.toString(member.getId()), refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));
        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(HttpServletRequest request, String accessToken) {

        // 토큰 복호화
        log.info("토큰 복호화");
        Claims claims = parseClaims(accessToken);
        log.info("claims = {}", claims.get(AUTHORITIES_KEY));
        if (claims.get(AUTHORITIES_KEY) == null) {
            return null;
        }
        System.out.println("test = ");
        // 클레임으로 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        log.info("authorities.toString() = " + authorities.toString());
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        log.info("principal = " + principal.toString());
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /* 토큰 유효성 검증, boolean */
    public boolean validateToken(HttpServletRequest request, String token) {

        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            request.setAttribute("exception", new ApiException(ExceptionEnum.TOKEN_SIGNITURE_ERROR));
            log.info("JWT 서명의 형식이 잘못되었습니다.");
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", new ApiException(ExceptionEnum.TOKEN_EXPIRED));
            log.info("만료된 JWT 입니다.");
        } catch (UnsupportedJwtException e) {
            request.setAttribute("exception", new ApiException(ExceptionEnum.TOKEN_NOT_SUPPORT));
            log.info("지원하지 않는 JWT 입니다.");
        } catch (IllegalArgumentException e) {
            request.setAttribute("exception", new ApiException(ExceptionEnum.TOKEN_ILLEGAL_JWT));
            log.info("잘못된 JWT 입니다.");
        }
        return false;
    }

    // 토큰 복호화
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Long getMemberIdByAccessToken(String accessToken) {
        // 토큰 복호화
        log.info("토큰 복호화");
        Claims claims = parseClaims(accessToken);
        log.info("claims = {}", claims.getSubject());
        if (claims.getSubject() == null) {
            return null;
        }
        return Long.valueOf(claims.getSubject());
    }

    public void checkRefreshToken(String memberId, String refreshToken) {
        String redisRT = redisService.getValues(memberId);
        if (!refreshToken.equals(redisRT)) {
            throw new ApiException(ExceptionEnum.TOKEN_EXPIRED);
        }
        redisService.deleteValues(memberId);
    }
}
