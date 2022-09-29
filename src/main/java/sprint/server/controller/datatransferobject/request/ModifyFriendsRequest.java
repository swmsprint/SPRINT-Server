package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import sprint.server.domain.friends.FriendState;

import javax.validation.constraints.NotNull;

@Data
public class ModifyFriendsRequest {
    @NotNull
    @ApiParam(value = "요청하는 회원 ID")
    @ApiModelProperty(example = "3", required = true)
    private Long sourceUserId;
    @NotNull
    @ApiParam(value = "API 요청 대상 회원 ID")
    @ApiModelProperty(example = "5", required = true)
    private Long targetUserId;
    @NotNull
    @ApiParam(value = "상태(ACCEPT/REJECT/CANCEL")
    @ApiModelProperty(example="REJECT")
    private FriendState friendState;
}
