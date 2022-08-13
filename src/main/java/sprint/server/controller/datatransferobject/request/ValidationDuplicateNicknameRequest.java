package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ValidationDuplicateNicknameRequest {
    @NotNull
    @ApiParam(value = "닉네임")
    @ApiModelProperty(example = "nickname", required = true)
    private String nickname;
}
