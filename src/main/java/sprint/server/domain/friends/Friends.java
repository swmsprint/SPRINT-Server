package sprint.server.domain.friends;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter @Setter @ToString
public class Friends implements Serializable {

    @Id @GeneratedValue
    private Long Id;

    @Column(name = "source_user_id")
    private Long sourceMemberId;

    @Column(name = "target_user_id")
    private Long targetMemberId;
    private Timestamp registeredDate;

    @Enumerated(EnumType.STRING)
    private FriendState establishState;


    public static Friends createFriendsRelationship(Long sourceMemberId, Long targetMemberId) {
        Friends friends = new Friends();
        friends.setSourceMemberId(sourceMemberId);
        friends.setTargetMemberId(targetMemberId);
        friends.setEstablishState(FriendState.REQUEST);
        return friends;
    }
}
