package sprint.server.domain;

import lombok.Getter;
import lombok.Setter;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.member.Member;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
public class Groups {

    @Id @GeneratedValue
    @Column(name = "group_id")
    private Integer id;

    private String groupName;
    @OneToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name="group_leader_id")
    private Member member;
    private String groupDescription;
    private String groupPicture;
    private int groupPersonnel;
    private int groupMaxPersonnel;
    private long ongoingGroupMatchId;
    private Timestamp groupJoinday;
    protected Groups() {
    }

    public Groups(String groupName, Member member, String groupDescription, String groupPicture) {
        this.groupName = groupName;
        this.member = member;
        this.groupDescription = groupDescription;
        this.groupPicture = groupPicture;
        this.groupPersonnel = 1;
        this.groupMaxPersonnel = 100;
        this.groupJoinday = Timestamp.valueOf(LocalDateTime.now());
    }

//    public void changeGroupLeaderId(Long groupLeaderId) {
//        this.groupLeaderId = groupLeaderId;
//    }

    public void setOngoingGroupMatchId(Long ongoingGroupMatchId) {
        this.ongoingGroupMatchId = ongoingGroupMatchId;
    }

    public void setGroupJoinday(Timestamp groupJoinday) {
        this.groupJoinday = groupJoinday;
    }

    public void changeGroupsInfo(String groupName, String groupDescription, String groupPicture) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupPicture = groupPicture;
    }
}
