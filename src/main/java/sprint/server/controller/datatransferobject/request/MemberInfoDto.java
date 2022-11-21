package sprint.server.controller.datatransferobject.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import sprint.server.domain.member.Gender;
import sprint.server.domain.member.Member;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class MemberInfoDto {
    @NotNull
    private String nickname;
    @NotNull
    private Gender gender;
    @NotNull
    @DateTimeFormat(pattern ="yyyy-MM-dd")
    private LocalDate birthday;
    @Positive
    private Float height;
    @Positive
    private Float weight;
    @NotNull
    private String picture;
    public MemberInfoDto(){}
    public MemberInfoDto(Member member) {
        this.nickname = member.getNickname();
        this.gender = member.getGender();
        this.birthday = member.getBirthday();
        this.height = member.getHeight();
        this.weight = member.getWeight();
        this.picture = member.getPicture();
    }
}
