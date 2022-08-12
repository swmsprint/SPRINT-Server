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

    public static Member createMember(String nickname, Gender gender, String email, LocalDate birthDay, float height, float weight, String picture) {
        Member member = new Member();
        member.setNickname(nickname);
        member.setGender(gender);
        member.setEmail(email);
        member.setBirthDay(birthDay);
        member.setJoinDay(Timestamp.valueOf(LocalDateTime.now()));
        member.setHeight(height);
        member.setWeight(weight);
        member.setPicture(picture);
        member.setTierId(0);
        return member;
    }
}