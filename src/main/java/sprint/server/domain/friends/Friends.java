package sprint.server.domain.friends;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter @ToString
public class Friends implements Serializable {

    @Id @GeneratedValue
    @NotNull
    private Long Id;

    @Column(name = "source_user_id")
    @NotNull
    private Long sourceMemberId;

    @Column(name = "target_user_id")
    @NotNull
    private Long targetMemberId;
    private Timestamp registeredDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FriendState establishState;

    protected Friends(){}

    public Friends(Long sourceMemberId, Long targetMemberId) {
        this.sourceMemberId = sourceMemberId;
        this.targetMemberId = targetMemberId;
        this.establishState = FriendState.REQUEST;
    }

    public void setRegisteredDate(Timestamp timestamp){
        this.registeredDate = timestamp;
    }

    public void setEstablishState(FriendState establishState) {
        this.establishState = establishState;
    }
}
