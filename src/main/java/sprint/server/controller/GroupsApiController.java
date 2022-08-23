package sprint.server.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.domain.Groups;
import sprint.server.service.GroupsService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class GroupsApiController {
    private final GroupsService groupsService;

    @PostMapping("/api/groups")
    public CreateGroupsResponse saveGroup(@RequestBody @Valid CreateGroupRequest request){
        Groups groups = new Groups();
        groups.setGroupLeaderId(request.getGroupLeaderId());
        groups.setGroupName(request.getGroupName());
        groups.setGroupDescription(request.getGroupDescription());
        groups.setGroupPicture(request.getGroupPicture());
        groups.setGroupPersonnel(1);
        groups.setGroupMaxPersonnel(100);


        int groupId = groupsService.addGroup(groups); // return 으로 id를 반환해해야하는지 아님 구분해야하는지 ?
        return new CreateGroupsResponse(groupId);
    }

    @Data class CreateGroupsResponse{

        private int groupId;

        public CreateGroupsResponse(int groupId) {
            this.groupId = groupId;
        }
    }

    @Data
    static class CreateGroupRequest {
        private Long groupLeaderId;
        private String groupName;
        private String groupDescription;
        private String groupPicture;

    }
}
