package sprint.server.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Groups extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "group_id")
    private Integer id;

    private String groupName;

    private Long groupLeaderId;
    private String groupDescription;
    private String groupPicture;
    private int groupPersonnel;
    private int groupMaxPersonnel;
    private Long ongoingGroupMatchId;
    private Boolean isDeleted;
    protected Groups() {
    }

    public Groups(String groupName, Long groupLeaderId, String groupDescription, String groupPicture) {
        this.groupName = groupName;
        this.groupLeaderId = groupLeaderId;
        this.groupDescription = groupDescription;
        this.groupPicture = groupPicture;
        this.groupPersonnel = 1;
        this.groupMaxPersonnel = 20;
        this.isDeleted = false;
    }

    public void changeDescriptionAndPicture(String groupDescription, String groupPicture){
        this.groupDescription = groupDescription;
        this.groupPicture = groupPicture;
    }
    public void changeGroupLeader(Long memberId) { this.groupLeaderId = memberId; }
    public void addMember(){
        this.groupPersonnel +=1;
    }
    public void leaveMember(){
        this.groupPersonnel -=1;
    }
    public void delete(){
        this.groupName = "DELETED_GROUP" + this.id;
        this.groupPersonnel = 0;
        this.isDeleted = true;
    }
}
