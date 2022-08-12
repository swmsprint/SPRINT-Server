package sprint.server.controller.datatransferobject.request;

import lombok.Data;

@Data
public class TwoMemberRequest {
    private Long sourceUserId;
    private Long targetUserId;
}
