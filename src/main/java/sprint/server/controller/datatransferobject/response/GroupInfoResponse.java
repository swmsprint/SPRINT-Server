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
    private Integer groupMaxPersonnel;
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
        this.groupMaxPersonnel = groups.getGroupMaxPersonnel();
        this.groupWeeklyStat = groupWeeklyStat;
        this.groupWeeklyUserData = groupWeeklyUserData;
    }
}
