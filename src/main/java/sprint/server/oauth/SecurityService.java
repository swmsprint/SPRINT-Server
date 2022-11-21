package sprint.server.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sprint.server.domain.member.Member;
import sprint.server.oauth.dto.token.TokenDto;
import sprint.server.service.MemberService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public static Long getCurrentAccountId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }
        return Long.parseLong(authentication.getName());
    }

    /* 로그인 된 사용자에게 토큰 발급 : refresh token 은 DB 에 저장 */
    public TokenDto login(Member member) {
        log.info("4-1. SecurityService-login: 계정을 찾았습니다.");
        log.info("4-2. 토큰 발행 시작");
        // 토큰 발행
        TokenDto tokenDto = jwtProvider.generateTokenDto(member);

        log.info("4-3. 토큰 발급과 저장을 완료");
        return tokenDto;
    }
}