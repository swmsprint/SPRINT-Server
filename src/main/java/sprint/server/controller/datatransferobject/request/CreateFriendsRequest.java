package sprint.server.controller.datatransferobject.request;

import lombok.Data;

@Data
public class CreateFriendsRequest {
    private Long sourceUserId;
    private Long targetUserId;
}
