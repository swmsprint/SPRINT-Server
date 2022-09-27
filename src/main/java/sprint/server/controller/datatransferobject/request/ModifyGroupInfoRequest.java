package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ModifyGroupInfoRequest {
    @NotNull
    @ApiParam(value="새롭게 그룹의 소개가 될 내용")
    private String groupDescription;
    @NotNull
    @ApiParam(value="새롭게 그룹의 사진이 될 사진의 주소")
    private String groupPicture;
}
