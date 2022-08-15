package sprint.server.domain.member;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @NotNull
    @Column(name = "member_id")
    private long id;
    @NotNull
    private String nickname;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @NotNull
    private String email;
    private LocalDate birthDay;
    @NotNull
    private Timestamp joinDay;
    @NotNull
    private float height;
    @NotNull
    private float weight;
    private int mainGroupId;
    @NotNull
    private int tierId;
    private String picture;

    private LocalDate disableDay;

    protected Member() {
    }

    public Member(String nickname, Gender gender, String email, LocalDate birthDay, float height, float weight, String picture) {
        this.nickname = nickname;
        this.gender = gender;
        this.email = email;
        this.birthDay = birthDay;
        this.height = height;
        this.weight = weight;
        this.picture = picture;
        this.joinDay = Timestamp.valueOf(LocalDateTime.now());
        this.tierId = 0;
    }
}