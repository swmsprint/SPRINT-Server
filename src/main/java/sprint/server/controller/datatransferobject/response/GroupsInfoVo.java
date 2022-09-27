package sprint.server.controller.datatransferobject.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberState;

import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
public class GroupsInfoVo {
    private int groupId;
    private String groupName;
    private String groupDescription;
    private String groupPicture;
    private int groupPersonnel;
    private int groupMaxPersonnel;
    @ApiModelProperty(example = "MEMBER")
    private GroupMemberState state;

    public GroupsInfoVo(Groups groups, List<Integer> myGroupList, List<Integer> requestedGroupList) {
        this.groupId = groups.getId();
        this.groupName = groups.getGroupName();
        this.groupDescription = groups.getGroupDescription();
        this.groupPicture = groups.getGroupPicture();
        this.groupPersonnel = groups.getGroupPersonnel();
        this.groupMaxPersonnel = groups.getGroupMaxPersonnel();
        this.state = myGroupList.contains(groups.getId()) ? GroupMemberState.MEMBER :
                requestedGroupList.contains(groups.getId()) ? GroupMemberState.REQUEST :
                GroupMemberState.NOT_MEMBER;
    }

    public static Comparator<GroupsInfoVo> COMPARE_BY_GROUPNAME = Comparator.comparing(o -> o.getGroupName());
}
