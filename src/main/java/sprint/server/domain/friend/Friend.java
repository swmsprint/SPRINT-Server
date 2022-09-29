package sprint.server.domain.friend;

import lombok.*;
import sprint.server.domain.BaseEntity;

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
        this.targetMemberId =targetMemberId;
        this.establishState = FriendState.REQUEST;
    }

    public void setMemberIds(Long sourceMemberId, Long targetMemberId) {
        this.sourceMemberId = sourceMemberId;
        this.targetMemberId = targetMemberId;
    }

    public void setEstablishState(FriendState establishState) {
        this.establishState = establishState;
    }
}
