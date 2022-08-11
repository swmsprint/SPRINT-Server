package sprint.server.domain.Member;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    private float height;
    private float weight;
    private int mainGroupId;
    private int tierId;
    private String picture;
}