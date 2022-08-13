package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OneMemberRequest {
    @NotNull
    @ApiParam(value = "회원Id")
    @ApiModelProperty(example = "3", required = true)
    private Long userId;
}
