package sprint.server.controller.datatransferobject.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ValidationDuplicateNicknameRequest {
    @NotNull
    private String nickname;
}
