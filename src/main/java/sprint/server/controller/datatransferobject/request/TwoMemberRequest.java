package sprint.server.controller.datatransferobject.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TwoMemberRequest {
    @NotNull
    private Long sourceUserId;
    @NotNull
    private Long targetUserId;
}
