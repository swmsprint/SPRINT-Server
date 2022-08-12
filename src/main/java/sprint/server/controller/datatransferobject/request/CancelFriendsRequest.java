package sprint.server.controller.datatransferobject.request;

import lombok.Data;

@Data
public class CancelFriendsRequest {
    private Long sourceUserId;
    private Long targetUserId;
}
