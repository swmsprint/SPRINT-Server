package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import sprint.server.domain.member.Gender;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class CreateMemberRequest {
    @NotNull
    @ApiParam(value = "닉네임")
    @ApiModelProperty(example = "nickname", required = true)
    private String nickname;

    @NotNull
    @ApiParam(value = "성별(MALE/FEMALE/X)")
    @ApiModelProperty(example = "MALE", required = true)
    private Gender gender;

    @NotNull @DateTimeFormat(pattern ="yyyy-MM-dd")
    @ApiParam(value = "생일")
    @ApiModelProperty(example = "2000-04-07")
    private LocalDate birthday;

    @Positive
    @ApiParam(value = "키")
    @ApiModelProperty(example = "176.5")
    private float height;

    @Positive
    @ApiParam(value = "몸무게")
    @ApiModelProperty(example = "70.4")
    private float weight;

    @ApiParam(value = "프로필 사진")
    @ApiModelProperty(example = "")
    private String picture;
}
