package sprint.server.domain.block;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import sprint.server.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GlobalBlock extends BaseEntity {
    @Id
    private Long globalBlockMemberId;
}
