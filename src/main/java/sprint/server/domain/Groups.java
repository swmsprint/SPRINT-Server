package sprint.server.domain;

import lombok.Getter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
        this.groupMaxPersonnel = 100;
        this.isDeleted = false;
    }

    public void delete(){
        this.isDeleted = true;
    }
}
