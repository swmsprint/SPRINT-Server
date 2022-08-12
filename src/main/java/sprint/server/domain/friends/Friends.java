package sprint.server.domain.friends;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter @Setter @ToString
public class Friends implements Serializable {

    @Id @GeneratedValue
    @NotNull
    private Long Id;

    @Column(name = "source_user_id")
    @NotNull
    private Long sourceMemberId;

    @Column(name = "target_user_id")
    @NotNull
    private Long targetMemberId;
    private Timestamp registeredDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FriendState establishState;


    public static Friends createFriendsRelationship(Long sourceMemberId, Long targetMemberId) {
        Friends friends = new Friends();
        friends.setSourceMemberId(sourceMemberId);
        friends.setTargetMemberId(targetMemberId);
        friends.setEstablishState(FriendState.REQUEST);
        return friends;
    }
}
