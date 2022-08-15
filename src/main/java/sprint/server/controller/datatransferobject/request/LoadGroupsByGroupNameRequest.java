package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoadGroupsByGroupNameRequest {
    @NotNull
    @ApiParam(value = "찾고자하는 그룹이름")
    @ApiModelProperty(example = "sprint", required = true)
    private String groupName;
}
