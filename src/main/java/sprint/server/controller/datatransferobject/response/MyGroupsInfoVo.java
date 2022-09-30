package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMemberState;

import java.util.Comparator;

@Data
@AllArgsConstructor
public class MyGroupsInfoVo {
    private int groupId;
    private String groupName;
    private String groupDescription;
    private String groupPicture;
    private int groupPersonnel;
    private int groupMaxPersonnel;
    private Boolean isLeader;

    public MyGroupsInfoVo(Groups groups, GroupMemberState State) {
        this.groupId = groups.getId();
        this.groupName = groups.getGroupName();
        this.groupDescription = groups.getGroupDescription();
        this.groupPicture = groups.getGroupPicture();
        this.groupPersonnel = groups.getGroupPersonnel();
        this.groupMaxPersonnel = groups.getGroupMaxPersonnel();
        this.isLeader = State == GroupMemberState.LEADER;
    }

    public static final Comparator<MyGroupsInfoVo> COMPARE_BY_ISLEADER = Comparator.comparing(o -> !o.getIsLeader());
}
