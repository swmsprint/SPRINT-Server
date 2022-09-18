package sprint.server.domain.groupmember;

import lombok.Getter;
import sprint.server.domain.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
public class GroupMember extends BaseEntity {

    @EmbeddedId()
    private GroupMemberId groupMemberId;
    @Enumerated(EnumType.STRING)
    private GroupMemberState groupMemberState;

    protected GroupMember(){}

    public GroupMember(GroupMemberId groupMemberId) {
        this.groupMemberId = groupMemberId;
        this.groupMemberState = GroupMemberState.REQUEST;
    }

    public void setGroupMemberState(GroupMemberState groupMemberState) {
        this.groupMemberState = groupMemberState;
    }
}
