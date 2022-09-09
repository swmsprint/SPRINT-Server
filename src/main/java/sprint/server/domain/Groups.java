package sprint.server.domain;

import lombok.Getter;

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

    private Long groupLeaderId;
    private String groupDescription;
    private String groupPicture;
    private int groupPersonnel;
    private int groupMaxPersonnel;
    private Long ongoingGroupMatchId;
    private Timestamp groupJoinday;
    protected Groups() {
    }

    public Groups(String groupName, Long groupLeaderId, String groupDescription, String groupPicture) {
        this.groupName = groupName;
        this.groupLeaderId = groupLeaderId;
        this.groupDescription = groupDescription;
        this.groupPicture = groupPicture;
        this.groupPersonnel = 1;
        this.groupMaxPersonnel = 100;
        this.groupJoinday = Timestamp.valueOf(LocalDateTime.now());
    }
}
