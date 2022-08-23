package sprint.server.controller.datatransferobject.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import sprint.server.domain.member.Gender;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class ModifyMembersRequest {
    @NotNull
    private Long id;
    @NotNull
    private String nickname;
    @NotNull
    private String email;
    @NotNull
    private Gender gender;
    @NotNull
    @DateTimeFormat(pattern ="yyyy-MM-dd")
    private LocalDate birthDay;
    @Positive
    private float height;
    @Positive
    private float weight;
    @NotNull
    private String picture;
}
