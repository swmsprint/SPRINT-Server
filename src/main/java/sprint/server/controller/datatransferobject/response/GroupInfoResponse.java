package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import sprint.server.domain.Groups;

@Data
@AllArgsConstructor
public class GroupInfoResponse<T, D> {
    private Integer groupId;
    private Long groupLeaderId;
    private String groupName;
    private Integer groupPersonnel;
    private String groupPicture;
    private String groupDescription;
    private T groupWeeklyStat;
    private D groupWeeklyUserData;
    public GroupInfoResponse(Groups groups, T groupWeeklyStat, D groupWeeklyUserData) {
        this.groupId = groups.getId();
        this.groupLeaderId = groups.getGroupLeaderId();
        this.groupName = groups.getGroupName();
        this.groupPersonnel = groups.getGroupPersonnel();
        this.groupPicture = groups.getGroupPicture();
        this.groupDescription = groups.getGroupDescription();
        this.groupWeeklyStat = groupWeeklyStat;
        this.groupWeeklyUserData = groupWeeklyUserData;
    }

    public void setGroupWeeklyStat(T groupWeeklyStat) {
        this.groupWeeklyStat = groupWeeklyStat;
    }

    public void setGroupWeeklyUserData(D groupWeeklyUserData) {
        this.groupWeeklyUserData = groupWeeklyUserData;
    }
}
