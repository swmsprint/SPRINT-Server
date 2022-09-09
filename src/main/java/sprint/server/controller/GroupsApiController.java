package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.ModifyGroupMemberRequest;
import sprint.server.controller.datatransferobject.request.CreateGroupMemberRequest;
import sprint.server.controller.datatransferobject.request.CreateGroupRequest;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberId;
import sprint.server.repository.GroupRepository;
import sprint.server.service.GroupService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-management/groups")
public class GroupsApiController {
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    @ApiOperation(value="그룹 만들기", notes ="groupName: NotNull\ngroupLeaderId: NotNull\ngroupDescription, groupPicture: Nullable")
    @PostMapping("")
    public CreateGroupsResponse createGroup(@RequestBody @Valid CreateGroupRequest request) {
        Groups groups = new Groups(request.getGroupName(), request.getGroupLeaderId(), request.getGroupDescription(), request.getGroupPicture());
        Integer groupId = groupService.join(groups);
        return new CreateGroupsResponse(groupId);

    }

    @ApiOperation(value="그룹 목록 검색", notes="닉네임 기준, LIKE연산")
    @GetMapping("")
    public GroupListResponse<GroupsInfoVo> findGroupsByGroupName(@RequestParam String target) {
        List<Groups> groups = groupRepository.findByGroupNameContaining(target);
        List<GroupsInfoVo> result = groups.stream()
                .map(GroupsInfoVo::new)
                .sorted(GroupsInfoVo.COMPARE_BY_GROUPNAME)
                .collect(Collectors.toList());
        return new GroupListResponse(result.size(), result);
    }

    @ApiOperation(value="그룹 가입 요청", notes = "groupId : NotNull\nuserId : NotNull")
    @PostMapping("/group-member")
    public BooleanResponse createGroupMember(@RequestBody @Valid CreateGroupMemberRequest request){
        GroupMember groupMember = new GroupMember(new GroupMemberId(request.getGroupId(), request.getUserId()));
        return new BooleanResponse(groupService.requestJoinGroupMember(groupMember));
    }

    @ApiOperation(value="그룹 가입 승인/거절/탈퇴", notes="groupUserState는 \"ACCEPT\", \"LEAVE\", \"REJECT\" 중 하나\n" +
            "* LEAVE: 그룹장은 탈퇴할 수 없음.")
    @PutMapping("/group-member")
    public BooleanResponse modifyGroupMember(@RequestBody @Valid ModifyGroupMemberRequest request) {
        GroupMemberId groupMemberId = new GroupMemberId(request.getGroupId(), request.getUserId());
        switch (request.getGroupMemberState()){
            case ACCEPT:
                return new BooleanResponse(groupService.answerGroupMember(groupMemberId, true));
            case REJECT:
                return new BooleanResponse(groupService.answerGroupMember(groupMemberId, false));
            case LEAVE:
                return new BooleanResponse(groupService.leaveGroupMember(groupMemberId));
            default:
                throw new ApiException(ExceptionEnum.GROUPS_METHOD_NOT_FOUND);
        }
    }
}
