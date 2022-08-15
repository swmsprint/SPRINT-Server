package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AnswerGroupRequest {
    @NotNull
    @ApiParam(value = "가입 신청 회원 ID")
    @ApiModelProperty(example = "2", required = true)
    private Long userId;
    @NotNull
    @ApiParam(value = "그룹 ID")
    @ApiModelProperty(example = "5", required = true)
    private Integer groupId;
    @NotNull
    @ApiParam(value = "수락/거절 여부")
    @ApiModelProperty(example = "(true/false)", required = true)
    private Boolean acceptance;
}
