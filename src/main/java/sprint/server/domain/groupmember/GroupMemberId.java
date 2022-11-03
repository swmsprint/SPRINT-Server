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
    private Integer groupId;

    private Long memberId;

    protected GroupMemberId(){}

    public GroupMemberId(Integer groupId, Long memberId) {
        this.groupId = groupId;
        this.memberId = memberId;
    }

    public GroupMemberId(Groups group, Member member) {
        this.groupId = group.getId();
        this.memberId = member.getId();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o==null || getClass()!=o.getClass()) return false;
        GroupMemberId groupMemberId = (GroupMemberId) o;
        return memberId.equals(groupMemberId.getMemberId()) && groupId.equals(groupMemberId.getGroupId());
    }

    @Override
    public int hashCode(){
        return Objects.hash(groupId, memberId);
    }
}
