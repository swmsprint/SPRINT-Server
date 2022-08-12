package sprint.server.controller.datatransferobject.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoadMembersByNicknameRequest {
    @NotNull
    private String nickname;
}
