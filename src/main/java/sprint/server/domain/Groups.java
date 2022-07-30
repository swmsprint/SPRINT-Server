package sprint.server.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Groups {

    @Id @GeneratedValue
    private int id;

    private long groupLeaderId;
    private String groupName;
    private int groupPersonnel;
    private int groupMaxPersonnel;
    private String groupDescription;
    private String groupPicture;
    private long ongoingGroupMatchId;

}
