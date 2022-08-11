package sprint.server.controller.datatransferobject.request;

import lombok.Data;
import sprint.server.domain.Member.Gender;

@Data
public class CreateMemberRequest {

    private String nickname;
    private String email;
    private Gender gender;
    private float height;
    private float weight;
    private String picture;
}
