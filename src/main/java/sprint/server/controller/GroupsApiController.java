package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.AnswerGroupRequest;
import sprint.server.controller.datatransferobject.request.CreateGroupMemberRequest;
import sprint.server.controller.datatransferobject.request.CreateGroupRequest;
import sprint.server.controller.datatransferobject.request.LoadGroupsByGroupNameRequest;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberId;
import sprint.server.domain.member.Member;
import sprint.server.service.GroupsService;
import sprint.server.service.MemberService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups/")
public class GroupsApiController {
    private final GroupsService groupsService;
    private final MemberService memberService;
    @ApiOperation(value="그룹 만들기")
    @PostMapping("/api/groups")
    public CreateGroupsResponse saveGroup(@RequestBody @Valid CreateGroupRequest request) {
        Groups groups = new Groups(request.getGroupName(), memberService.findById(request.getGroupLeaderId()), request.getGroupDescription(), request.getGroupPicture());
        int groupId = groupsService.join(groups);
        return new CreateGroupsResponse(groupId);

    }

    @ApiOperation(value="그룹 목록 검색", notes="닉네임 기준, LIKE연산")
    @GetMapping("/api/groups/list")
    public LoadGroupsResponse<LoadGroupsResponseVo> loadGroupsByGroupName(@RequestBody @Valid LoadGroupsByGroupNameRequest request) {
        List<Groups> groups = groupsService.findByGroupNameContaining(request.getGroupName());
        List<LoadGroupsResponseVo> result = groups.stream()
                .map(LoadGroupsResponseVo::new)
                .sorted(LoadGroupsResponseVo.COMPARE_BY_GROUPNAME)
                .collect(Collectors.toList());
        return new LoadGroupsResponse(result.size(), result);
    }

    @ApiOperation(value="그룹 가입 요청")
    @PostMapping("/api/groups/request")
    public BooleanResponse createGroupMember(@RequestBody @Valid CreateGroupMemberRequest request){
        Groups groups = groupsService.findById(request.getGroupId());
        Member member = memberService.findById(request.getMemberId());
        GroupMember groupMember = new GroupMember(new GroupMemberId(groups, member));
        return new BooleanResponse(groupsService.joinGroupMember(groupMember));
    }

    @ApiOperation(value="그룹 가입 승인")
    @PutMapping("/api/groups/response")
    public BooleanResponse answerGroupMember(@RequestBody @Valid AnswerGroupRequest request) {
        return new BooleanResponse(groupsService.answerGroupMember(request.getGroupId(), request.getUserId(), request.getAcceptance()));
    }
}
