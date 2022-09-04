package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateFriendsRequest {
    @NotNull
    @ApiParam(value = "요청하는 회원 ID")
    @ApiModelProperty(example = "3", required = true)
    private Long sourceUserId;
    @NotNull
    @ApiParam(value = "API 요청 대상 회원 ID")
    @ApiModelProperty(example = "5", required = true)
    private Long targetUserId;
}
