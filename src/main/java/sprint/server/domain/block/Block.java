package sprint.server.domain.block;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import sprint.server.domain.BaseEntity;
import sprint.server.domain.friend.FriendId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@IdClass(FriendId.class)
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "source_member_index", columnList = "sourceMemberId"),
        @Index(name = "target_member_index", columnList = "targetMemberId")
})
public class Block extends BaseEntity {
    @Id
    @NotNull
    private Long sourceMemberId;
    @Id
    @NotNull
    private Long targetMemberId;

    public Long getTargetMemberId() {
        return targetMemberId;
    }
}
