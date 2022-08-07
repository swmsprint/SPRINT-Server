package sprint.server.controller.datatransferobject.request;

import lombok.Data;

@Data
public class CreateFriendsResultRequest {
    private Long sourceUserId;
    private Long targetUserId;
}
