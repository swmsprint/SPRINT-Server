package sprint.server.domain.Member;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

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