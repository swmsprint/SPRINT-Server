package sprint.server.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Report extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private Long sourceMemberId;
    private Long targetMemberId;
    private String message;

    public Report(Long sourceMemberId, Long targetMemberId, String message) {
        this.sourceMemberId = sourceMemberId;
        this.targetMemberId = targetMemberId;
        this.message = message;
    }
}