package sprint.server.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sprint.server.controller.exception.CEmailLoginFailedException;
import sprint.server.domain.member.Member;
import sprint.server.domain.member.Provider;
import sprint.server.domain.member.ProviderPK;
import sprint.server.oauth.dto.member.LoginResponseDto;
import sprint.server.oauth.dto.token.TokenDto;
import sprint.server.repository.GlobalBlockRepository;
import sprint.server.service.MemberService;

@Service
@RequiredArgsConstructor
@Slf4j
public class OauthService {
    private final MemberService memberService;
    private final SecurityService securityService;
    private final JwtProvider jwtProvider;
    private final GlobalBlockRepository globalBlockRepository;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${kakao.client-id}")
    private String kakaoClientId;

    public ResponseEntity<LoginResponseDto> firebaseLogin(Provider provider, String UID) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        log.info("UID : {}", UID);
        log.info("1. DB에서 해당 계정 정보 확인");
        ProviderPK providerPK = new ProviderPK(provider, UID);
        Member firebaseMember = memberService.findByProviderPK(providerPK);
        if(firebaseMember == null) {
            log.info("2-1. 회원정보 없음 : 신규가입");
            firebaseMember = registerUserIfNeed(provider, UID);
            loginResponseDto.setAlreadySignIn(false);
        }else {
            log.info("2-2 회원정보 화인 : 기존회원");
            loginResponseDto.setAlreadySignIn(true);
            log.info("3. 비활성화 여부 확인");
            if (firebaseMember.getDisableDay() == null){
                log.info("3-1. 활성화 계정");
            } else {
                log.info("3-1-1. 정지 계정 확인");
                if (globalBlockRepository.existsById(firebaseMember.getId())){
                    log.info("3-1-2. 정지 계정");
                    loginResponseDto.setGlobalBlock(true);
                    return ResponseEntity.ok().body(loginResponseDto);
                } else {
                    log.info("3-1-2. 비활성화 계정 : 재활성화");
                    memberService.activateMember(firebaseMember);
                }
            }
        }

        if (firebaseMember.getNickname() == null) {
            memberService.disableMember(firebaseMember);
            loginResponseDto.setAlreadySignIn(false);
        }

        try {
            log.info("4. 해당 계정으로 토큰 발급 시작");
            TokenDto tokenDto = securityService.login(firebaseMember);
            loginResponseDto.setAccessToken(tokenDto.getAccessToken());
            loginResponseDto.setRefreshToken(tokenDto.getRefreshToken());
            loginResponseDto.setUserId(firebaseMember.getId());
            return ResponseEntity.ok().body(loginResponseDto);
        } catch (CEmailLoginFailedException e) {
            log.info("5. 에러 발생");
            return ResponseEntity.ok(loginResponseDto);
        }
    }

    public ResponseEntity<LoginResponseDto> kakaoLogin(String code) throws JsonProcessingException {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        log.info("Provider : KAKAO");
        log.info("1. 인가 코드로 카카오 access token 요청");

        String accessToken = getAccessToken(code);
        log.info("accessToken = {}", accessToken);

        log.info("2. 카카오 access 토큰으로 카카오 API 호출");
        SocialUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        log.info("3. DB에서 해당 계정 정보 확인");
        ProviderPK providerPK = new ProviderPK(Provider.KAKAO, kakaoUserInfo.getUid());
        Member kakaoMember = memberService.findByProviderPK(providerPK);
        if(kakaoMember == null) {
            log.info("3-1. 회원정보 없음 : 신규가입");
            kakaoMember = registerUserIfNeed(Provider.KAKAO, kakaoUserInfo.getUid());
            loginResponseDto.setAlreadySignIn(false);
        }else {
            log.info("3-2. 회원정보 확인 : 기존회원");
            loginResponseDto.setAlreadySignIn(true);
            log.info("3-3. 비활성화 여부 확인");
            if (kakaoMember.getDisableDay() == null){
                log.info("3-3-1. 활성화 계정");
            } else {
                log.info("3-1-1. 정지 계정 확인");
                if (globalBlockRepository.existsById(kakaoMember.getId())){
                    log.info("3-1-2. 정지 계정");
                    loginResponseDto.setGlobalBlock(true);
                    return ResponseEntity.ok().body(loginResponseDto);
                } else {
                    log.info("3-1-2. 비활성화 계정 : 재활성화");
                    memberService.activateMember(kakaoMember);
                }
            }
        }

        if (kakaoMember.getNickname() == null) {
            memberService.disableMember(kakaoMember);
            loginResponseDto.setAlreadySignIn(false);
        }

        try {
            log.info("4. 해당 계정으로 토큰 발급 시작");
            TokenDto tokenDto = securityService.login(kakaoMember);
            loginResponseDto.setAccessToken(tokenDto.getAccessToken());
            loginResponseDto.setRefreshToken(tokenDto.getRefreshToken());
            loginResponseDto.setUserId(kakaoMember.getId());
            return ResponseEntity.ok().body(loginResponseDto);
        } catch (CEmailLoginFailedException e) {
            log.info("5. 에러 발생");
            return ResponseEntity.ok(loginResponseDto);
        }
    }
    public String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", kakaoRedirectUri);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 2. 토큰으로 카카오 API 호출
    private SocialUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String uid = Long.toString(jsonNode.get("id").asLong());
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();

        return new SocialUserInfoDto(uid, nickname);
    }

//     3. 회원가입 처리
    private Member registerUserIfNeed(Provider provider, String UID) {
        ProviderPK providerPK = new ProviderPK(provider, UID);
        String profile = "https://sprint-images.s3.ap-northeast-2.amazonaws.com/users/default.jpeg";
        log.info("새 계정 생성");
        Member newUser = new Member(profile, providerPK);
        log.info("새 계정 저장");
        memberService.join(newUser);
        return newUser;
    }

    public TokenDto reIssueAccessToken(Member member, String refreshToken) {
        jwtProvider.checkRefreshToken(Long.toString(member.getId()), refreshToken);
        return jwtProvider.generateTokenDto(member);
    }

}