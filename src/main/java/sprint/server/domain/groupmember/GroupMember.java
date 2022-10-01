package sprint.server.domain.groupmember;

import lombok.Getter;
import sprint.server.domain.BaseEntity;
import sprint.server.domain.Groups;
import sprint.server.domain.member.Member;

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
    public GroupMember(Groups group, Member member) {
        this.groupMemberId = new GroupMemberId(group, member);
        this.groupMemberState = GroupMemberState.REQUEST;
    }
    public Integer getGroupId() { return this.getGroupMemberId().getGroupId();}

    public Long getMemberId() { return this.getGroupMemberId().getMemberId();}
    public void setGroupMemberState(GroupMemberState groupMemberState) {
        this.groupMemberState = groupMemberState;
    }
}
