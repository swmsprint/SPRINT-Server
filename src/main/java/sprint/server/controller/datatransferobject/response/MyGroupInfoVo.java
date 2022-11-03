package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMemberState;

import java.util.Comparator;

@Data
@AllArgsConstructor
public class MyGroupInfoVo {
    private int groupId;
    private String groupName;
    private String groupDescription;
    private String groupPicture;
    private int groupPersonnel;
    private int groupMaxPersonnel;
    private GroupMemberState state;

    public MyGroupInfoVo(Groups groups, GroupMemberState state) {
        this.groupId = groups.getId();
        this.groupName = groups.getGroupName();
        this.groupDescription = groups.getGroupDescription();
        this.groupPicture = groups.getGroupPicture();
        this.groupPersonnel = groups.getGroupPersonnel();
        this.groupMaxPersonnel = groups.getGroupMaxPersonnel();
        this.state = state.equals(GroupMemberState.ACCEPT) ? GroupMemberState.MEMBER : state;
    }

    public static final Comparator<MyGroupInfoVo> COMPARE_BY_ISLEADER = Comparator.comparing(o -> o.getState());
}
