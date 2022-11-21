package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateGroupRequest {
    @NotNull
    @ApiParam(value = "그룹 리더 ID")
    @ApiModelProperty(example = "3", required = true)
    private Long groupLeaderId;
    @NotNull
    @ApiParam(value = "그룹 이름")
    @ApiModelProperty(example = "스프린트화이팅", required = true)
    private String groupName;
    @ApiParam(value = "그룹 설명")
    @ApiModelProperty(example = "그룹설명입니다")
    private String groupDescription;
    @ApiParam(value = "그룹 대표 사진")
    @ApiModelProperty(example = "사진링크")
    private String groupPicture;
}