package sprint.server.oauth;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import sprint.server.domain.member.Provider;

import javax.validation.constraints.NotNull;

@Data
public class FirebaseSignRequest {
    @NotNull
    @ApiParam(value = "로그인 요청 Provider, (GOOGLE/APPLE)")
    @ApiModelProperty(example = "GOOGLE", required = true)
    private Provider provider;
    @NotNull
    @ApiParam(value = "firebase UID")
    private String UID;
}