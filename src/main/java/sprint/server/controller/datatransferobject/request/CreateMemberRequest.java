package sprint.server.controller.datatransferobject.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import sprint.server.domain.member.Gender;
import java.time.LocalDate;

@Data
public class CreateMemberRequest {
    private String nickname;
    private String email;
    private Gender gender;
    @DateTimeFormat(pattern ="yyyy-MM-dd")
    private LocalDate birthDay;
    private float height;
    private float weight;
    private String picture;
}
