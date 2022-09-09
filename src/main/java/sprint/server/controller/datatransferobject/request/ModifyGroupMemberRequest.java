package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import sprint.server.domain.groupmember.GroupMemberState;

import javax.validation.constraints.NotNull;

@Data
public class ModifyGroupMemberRequest {
    @NotNull
    @ApiParam(value = "회원 ID")
    @ApiModelProperty(example = "2", required = true)
    private Long userId;
    @NotNull
    @ApiParam(value = "그룹 ID")
    @ApiModelProperty(example = "5", required = true)
    private Integer groupId;
    @NotNull
    @ApiParam(value = "ACCEPT : 수락, 이전의 가입요청 상태이어야 함." +
            ", LEAVE : 탈퇴, 이전에 가입완료 상태이어야 함." +
            ", REJECT : 거절, 이전에 가입요청 상태이어야 함.;")
    @ApiModelProperty(example = "(ACCEPT/LEAVE/REJECT)", required = true)
    private GroupMemberState groupMemberState;
}
