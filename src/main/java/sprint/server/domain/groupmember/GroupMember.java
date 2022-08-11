package sprint.server.domain.groupmember;

import lombok.Getter;
import lombok.Setter;
import sprint.server.domain.Groups;
import sprint.server.domain.Member.Member;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter @Setter
public class GroupMember implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups group;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    private Timestamp registeredDate;
    private GroupMemberState memberState;

}
