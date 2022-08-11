package sprint.server.domain.Member;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private long id;

    private String nickname;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String email;
    private LocalDate birthDay;
    private Timestamp joinDay;
    private float height;
    private float weight;
    private int mainGroupId;
    private int tierId;
    private String picture;
}