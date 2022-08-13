package sprint.server.controller.datatransferobject.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class ValidationDuplicateEmailRequest {
    @NotNull @Email
    @ApiParam(value = "이메일")
    @ApiModelProperty(example = "email@email.com", required = true)
    private String email;
}
