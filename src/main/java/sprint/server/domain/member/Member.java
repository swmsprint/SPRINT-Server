package sprint.server.domain.member;

import lombok.Getter;
import sprint.server.domain.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @NotNull
    @Column(name = "member_id")
    private Long id;
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    @Enumerated(EnumType.STRING)
    private Authority authority;
    private LocalDate birthday;
    private float height;
    private float weight;
    private int mainGroupId;
    private int tierId;
    private String picture;

    private LocalDate disableDay;

    @Embedded
    @Column(unique = true)
    @NotNull
    private ProviderPK providerPK;

    protected Member() {
    }

    public Member(String nickname, Gender gender, LocalDate birthday, float height, float weight, String picture) {
        this.nickname = nickname;
        this.gender = gender;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.picture = picture;
        this.tierId = 0;
    }

    public Member(String picture, ProviderPK providerPK) {
        this.picture = picture;
        this.providerPK = providerPK;
        this.authority = Authority.ROLE_USER;
    }

    public void changeMemberInfo(String nickname, Gender gender, LocalDate birthDay, float height, float weight, String picture){
        this.nickname = nickname;
        this.gender = gender;
        this.birthday = birthDay;
        this.height = height;
        this.weight = weight;
        this.picture = picture;
    }
    public void disable(){ this.disableDay = LocalDate.now(); }
    public void enable() { this.disableDay = null; }

    public void changeMainGroupId(int mainGroupId) {
        this.mainGroupId = mainGroupId;
    }
    public void setNickname(String nickname){ this.nickname = nickname;}
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        Member m = (Member) obj;
        return m.getId().equals(id);
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }
}