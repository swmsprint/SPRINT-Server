package sprint.server.domain.friends;

import lombok.*;

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
public class Friends {

    @Id @GeneratedValue
    private Long id;
    @NotNull
    private Long sourceMemberId;

    @NotNull
    private Long targetMemberId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FriendState establishState;

    private Timestamp registeredDate;

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
    public void setRegisteredDate(Timestamp timestamp){
        this.registeredDate = timestamp;
    }

    public void setEstablishState(FriendState establishState) {
        this.establishState = establishState;
    }
}
