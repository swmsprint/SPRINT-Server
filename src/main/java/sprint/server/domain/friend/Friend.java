package sprint.server.domain.friend;

import lombok.*;
import sprint.server.domain.BaseEntity;
import sprint.server.domain.member.Member;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter @ToString
@IdClass(FriendId.class)
@Table(indexes = {
        @Index(name = "source_member_index", columnList = "sourceMemberId"),
        @Index(name = "target_member_index", columnList = "targetMemberId")
})
public class Friend extends BaseEntity {

    @Id
    @NotNull
    private Long sourceMemberId;

    @Id
    @NotNull
    private Long targetMemberId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FriendState establishState;


    protected Friend(){}

    public Friend(Long sourceMemberId, Long targetMemberId) {
        this.sourceMemberId = sourceMemberId;
        this.targetMemberId = targetMemberId;
        this.establishState = FriendState.REQUEST;
    }

    public Friend(Member sourceMember, Member targetMember) {
        this.sourceMemberId = sourceMember.getId();
        this.targetMemberId = targetMember.getId();
        this.establishState = FriendState.REQUEST;
    }

    public void setMemberIds(Long sourceMemberId, Long targetMemberId) {
        this.sourceMemberId = sourceMemberId;
        this.targetMemberId = targetMemberId;
    }

    public void setAccept() {this.establishState = FriendState.ACCEPT;}
    public void setReject() {this.establishState = FriendState.REJECT;}
    public void setCancel() {this.establishState = FriendState.CANCEL;}
    public void setDelete() {this.establishState = FriendState.DELETE;}
}
