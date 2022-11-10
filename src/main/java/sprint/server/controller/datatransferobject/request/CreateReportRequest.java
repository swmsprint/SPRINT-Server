package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateReportRequest {
    @NotNull
    @ApiParam(value="신고 대상 유저")
    @ApiModelProperty(example ="3", required = true)
    private Long targetMemberId;
    @NotNull
    @ApiParam(value="신고 내용")
    @ApiModelProperty(example = "템플릿사용 or 직접입력 내용", required = true)
    private String message;
}
