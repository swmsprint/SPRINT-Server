package sprint.server.domain.friends;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sprint.server.domain.Member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter @Setter @ToString
public class Friends implements Serializable {

    @Id
    @Column(name = "source_user_id")
    private Long sourceMemberId;

    @Id
    @Column(name = "target_user_id")
    private Long targetMemberId;
    private Timestamp registeredDate;
    private FriendState establishState;


    public static Friends createFriendsRelationship(Member sourceMember, Member targetMember) {
        Friends friends = new Friends();
        friends.setSourceMemberId(sourceMember.getId());
        friends.setTargetMemberId(targetMember.getId());
        friends.setEstablishState(FriendState.REQUEST);
        return friends;
    }
}
