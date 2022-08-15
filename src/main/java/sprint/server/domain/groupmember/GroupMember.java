package sprint.server.domain.groupmember;

import lombok.Getter;
import lombok.Setter;
import sprint.server.domain.Groups;
import sprint.server.domain.member.Member;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
public class GroupMember{

    @EmbeddedId()
    private GroupMemberId groupMemberId;
    private Timestamp registeredDate;
    @Enumerated(EnumType.STRING)
    private GroupMemberState memberState;

    protected GroupMember(){}

    public GroupMember(GroupMemberId groupMemberId) {
        this.groupMemberId = groupMemberId;
        this.memberState = GroupMemberState.REQUEST;
    }

    public void setRegisteredDate(Timestamp registeredDate) {
        this.registeredDate = registeredDate;
    }

    public void setMemberState(GroupMemberState memberState) {
        this.memberState = memberState;
    }
}
