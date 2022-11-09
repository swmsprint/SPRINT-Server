package sprint.server.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sprint.server.domain.member.Member;
import sprint.server.domain.member.Provider;
import sprint.server.oauth.dto.member.LoginResponseDto;
import sprint.server.oauth.dto.token.TokenDto;
import sprint.server.service.MemberService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
@Slf4j
public class Oauth2ApiController {
    private final OauthService oauthService;
    private final MemberService memberService;

    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-uri}")
    private String kakaoURI;
    @GetMapping("/kakao")
    public void kakaoLogin(HttpServletResponse httpServletResponse) throws IOException {
        String s = "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId + "&redirect_uri=" + kakaoURI + "&response_type=code";
        httpServletResponse.sendRedirect(s);
    }

    @GetMapping("/callback/kakao")
    public ResponseEntity<LoginResponseDto> kakaoSign(@RequestParam String code) throws JsonProcessingException {
        log.info("카카오 로그인 시도 ");
        return oauthService.kakaoLogin(code);
    }

    @GetMapping("/firebase")
    public ResponseEntity<LoginResponseDto> firebaseSign(@RequestParam("provider") Provider provider, @RequestParam("uid") String uid) {
        log.info("firebase 로그인 시도");
        log.info("Provider : {}", provider);
        return oauthService.firebaseLogin(provider, uid);
    }

    @GetMapping("/re-issue")
    public ResponseEntity<TokenDto> reIssue(@RequestParam("userId") Long userId, @RequestParam("refreshToken") String refreshToken) {
        log.info("토큰 복호화");
        Member member = memberService.findById(userId);
        TokenDto tokenDto = oauthService.reIssueAccessToken(member, refreshToken);
        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }
}
