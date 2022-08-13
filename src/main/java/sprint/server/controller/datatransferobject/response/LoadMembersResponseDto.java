package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoadMembersResponseDto<T> {
    private int count;
    private T userFriends;
}
