package sprint.server.controller.datatransferobject.request;

import lombok.Data;

@Data
public class DeleteFriendsRequest {
    private Long sourceUserId;
    private Long targetUserId;
}
