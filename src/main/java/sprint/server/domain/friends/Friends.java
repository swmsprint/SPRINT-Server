package sprint.server.domain.friends;

import lombok.*;
import sprint.server.domain.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Getter @ToString
@Table(indexes = {
        @Index(name = "composite_index", columnList = "sourceMemberId, targetMemberId"),
        @Index(name = "source_member_index", columnList = "sourceMemberId"),
        @Index(name = "target_member_index", columnList = "targetMemberId")
})
public class Friends extends BaseEntity {

    @Id @GeneratedValue
    private Long id;
    @NotNull
    private Long sourceMemberId;

    @NotNull
    private Long targetMemberId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FriendState establishState;


    protected Friends(){}

    public Friends(Long sourceMemberId, Long targetMemberId) {
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
