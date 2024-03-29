package sprint.server.domain.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendId implements Serializable {
    private Long sourceMemberId;
    private Long targetMemberId;
}
