package sprint.server.domain.friends;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Getter @ToString
@IdClass(FriendsId.class)
@Table(indexes = {
        @Index(name = "source_member_index", columnList = "sourceMemberId"),
        @Index(name = "target_member_index", columnList = "targetMemberId")
})
public class Friends {

    @Id
    @NotNull
    private Long sourceMemberId;

    @Id
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

    public void setRegisteredDate(Timestamp timestamp){
        this.registeredDate = timestamp;
    }

    public void setEstablishState(FriendState establishState) {
        this.establishState = establishState;
    }
}
