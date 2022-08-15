package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateGroupMemberRequest {
    @NotNull
    @ApiParam(value = "그룹 ID")
    @ApiModelProperty(example = "3", required = true)
    private Integer groupId;
    @NotNull
    @ApiParam(value = "회원 ID")
    @ApiModelProperty(example = "2", required = true)
    private Long memberId;
}
