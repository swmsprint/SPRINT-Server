package sprint.server.domain.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendsId implements Serializable {
    private Long sourceMemberId;
    private Long targetMemberId;
}
