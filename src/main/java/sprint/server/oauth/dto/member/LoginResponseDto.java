package sprint.server.oauth.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private boolean alreadySignIn;
    private Long userId;
    private String accessToken;
    private String refreshToken;
}
