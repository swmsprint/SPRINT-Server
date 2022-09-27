package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ModifyGroupLeaderRequest {
    @NotNull
    @ApiParam(value = "변경하고자 하는 그룹 ID")
    private Integer groupId;
    @NotNull
    @ApiParam(value = "그룹장이 될 그룹원 ID")
    private Long newGroupLeaderUserId;
}
