package sprint.server.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class SocialUserInfoDto {
    private String uid;
    private String nickname;

    public SocialUserInfoDto(String uid, String nickname) {
        this.uid = uid;
        this.nickname = nickname;
    }
}