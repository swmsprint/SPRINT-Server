package sprint.server.domain.groupmember;

import lombok.Data;
import sprint.server.domain.Groups;
import sprint.server.domain.member.Member;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class GroupMemberId implements Serializable {
    @OneToOne(targetEntity = Groups.class, fetch = FetchType.LAZY)
    @JoinColumn(name="group_id")
    private Groups groups;

    @OneToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    protected GroupMemberId(){}

    public GroupMemberId(Groups groups, Member member) {
        this.groups = groups;
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o==null || getClass()!=o.getClass()) return false;
        GroupMemberId groupMemberId = (GroupMemberId) o;
        return member.getId().equals(groupMemberId.member.getId()) && groups.getId() == groupMemberId.getGroups().getId();
    }

    @Override
    public int hashCode(){
        return Objects.hash(groups, member);
    }
}
