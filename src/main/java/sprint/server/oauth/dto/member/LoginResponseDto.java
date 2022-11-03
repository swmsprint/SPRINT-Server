package sprint.server.oauth.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sprint.server.domain.member.Member;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private boolean alreadySignIn;
    private Long userId;
    private String accessToken;
    private String refreshToken;
}
