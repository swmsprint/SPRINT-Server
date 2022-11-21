package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.*;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberState;
import sprint.server.domain.member.Member;
import sprint.server.service.GroupService;
import sprint.server.service.MemberService;
import sprint.server.service.StatisticsService;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user-management/group")
public class GroupsApiController {
    private final MemberService memberService;
    private final GroupService groupService;
    private final StatisticsService statisticsService;

    @ApiOperation(value="그룹 만들기", notes ="groupName: NotNull\ngroupLeaderId: NotNull\ngroupDescription, groupPicture: Nullable")
    @PostMapping("")
    public CreateGroupsResponse createGroup(@RequestBody @Valid CreateGroupRequest request) {
        log.info("ID : {}, 그룹 만들기 요청", request.getGroupLeaderId());
        Groups groups = new Groups(request.getGroupName(), request.getGroupLeaderId(), request.getGroupDescription(), request.getGroupPicture());
        Integer groupId = groupService.join(groups);
        return new CreateGroupsResponse(groupId);

    }

    @ApiOperation(value="내가 가입한 그룹 목록 검색", notes = "그룹장인 그룹이 더 우선으로 존재")
    @GetMapping("/list/{userId}")
    public GroupListResponse<MyGroupInfoVo> findGroupsByUserId(@PathVariable Long userId) {
        log.info("ID : {}, 가입한 그룹 목록 검색", userId);
        Member member = memberService.findById(userId);
        List<MyGroupInfoVo> result = groupService.findJoinedGroupMemberByMember(member)
                .stream()
                .map(groupMember -> new MyGroupInfoVo(
                        groupService.findGroupByGroupId(groupMember.getGroupId()), groupMember.getGroupMemberState()))
                .sorted(MyGroupInfoVo.COMPARE_BY_ISLEADER)
                .collect(Collectors.toList());
        log.info("ID : {}, 가입한 그룹 목록 검색 완료, 결과 : {}개 발견", userId, result.size());
        return new GroupListResponse(result.size(), result);
    }

    @ApiOperation(value="내가 요청한 그룹 목록 검색")
    @GetMapping("/list/{userId}/request")
    public GroupListResponse<MyGroupInfoVo> findGroupsRequestByUserId(@PathVariable Long userId) {
        log.info("ID : {}, 가입 요청한 그룹 목록 검색", userId);
        Member member = memberService.findById(userId);
        List<MyGroupInfoVo> result = groupService.findRequestGroupMemberByMember(member)
                .stream()
                .map(groups -> new MyGroupInfoVo(
                        groupService.findGroupByGroupId(groups.getId()), GroupMemberState.REQUEST))
                .sorted(MyGroupInfoVo.COMPARE_BY_ISLEADER)
                .collect(Collectors.toList());
        log.info("ID : {}, 가입 요청한 그룹 목록 검색 완료, 결과 : {}개 발견", userId, result.size());
        return new GroupListResponse(result.size(), result);
    }

    @ApiOperation(value="요청받은 그룹 가입 목록 검색")
    @GetMapping("/list/{groupId}/requested")
    public FindMembersResponseDto<FindFriendsResponseVo> findRequestMemberByGroup(@PathVariable Integer groupId) {
        log.info("Group ID : {}, 가입 요청한 회원 목록 검색", groupId);
        Groups group = groupService.findGroupByGroupId(groupId);
        List<Member> memberList = groupService.findRequestMemberByGroup(group);
        List<FindFriendsResponseVo> result = memberList.stream().map(FindFriendsResponseVo::new).collect(Collectors.toList());
        return new FindMembersResponseDto(result.size(), result);
    }

    @ApiOperation(value="전체 그룹 목록 검색", notes="그룹 이름 기준, LIKE연산\nstate는 \"LEADER\", \"MEMBER\", \"NOT_MEMBER\", \"REQUEST\" 중 하나로 응답.")
    @GetMapping("/list")
    public GroupListResponse<GroupInfoVo> findGroupsByGroupName(@RequestParam Long userId, @RequestParam String target) {
        log.info("ID : {}, 전체 그룹 목록 검색 target : {}", userId, target);
        Member member = memberService.findById(userId);
        List<Groups> groupList = groupService.findGroupByString(target);
        List<GroupMember> groupMemberList = groupService.findJoinedGroupMemberByMember(member);
        List<Integer> leaderGroupId = groupMemberList.stream()
                .filter(groupMember -> groupMember.getGroupMemberState().equals(GroupMemberState.LEADER))
                .map(GroupMember::getGroupId)
                .collect(Collectors.toList());
        List<Integer> memberGroupList = groupMemberList.stream()
                .filter(groupMember -> groupMember.getGroupMemberState().equals(GroupMemberState.ACCEPT))
                .map(GroupMember::getGroupId)
                .collect(Collectors.toList());
        List<Integer> requestGroupList = groupService.findRequestGroupMemberByMember(member)
                .stream().map(Groups::getId).collect(Collectors.toList());
        List<GroupInfoVo> result = groupList.stream()
                .map(g -> new GroupInfoVo(g, memberGroupList, requestGroupList, leaderGroupId))
                .sorted(GroupInfoVo.COMPARE_BY_GROUPNAME)
                .collect(Collectors.toList());
        log.info("ID : {}, 전체 그룹 목록 검색 완료, 결과 : {}개 발견", userId, result.size());
        return new GroupListResponse(result.size(), result);
    }

    @ApiOperation(value="그룹 가입 요청", notes = "groupId : NotNull\nuserId : NotNull")
    @PostMapping("/group-member")
    public BooleanResponse createGroupMember(@RequestBody @Valid GroupIdMemberIdRequest request){
        log.info("그룹 가입 요청");
        Groups group = groupService.findGroupByGroupId(request.getGroupId());
        Member member = memberService.findById(request.getUserId());
        return new BooleanResponse(groupService.requestJoinGroupMember(group, member));
    }

    @ApiOperation(value="그룹 가입 승인/거절/취소", notes="groupMemberState는 \"ACCEPT\", \"REJECT\", \"CANCEL\" 중 하나\n" +
            "* LEAVE: 그룹장은 탈퇴할 수 없음.")
    @PutMapping("/group-member")
    public BooleanResponse modifyGroupMember(@RequestBody @Valid ModifyGroupMemberRequest request) {
        log.info("그룹 가입 응답 요청");
        Groups group = groupService.findGroupByGroupId(request.getGroupId());
        Member member = memberService.findById(request.getUserId());

        switch (request.getGroupMemberState()){
            case ACCEPT:
            case REJECT:
            case CANCEL:
                return new BooleanResponse(groupService.answerGroupMember(group, member, request.getGroupMemberState()));
            default:
                throw new ApiException(ExceptionEnum.GROUP_METHOD_NOT_FOUND);
        }
    }

    @ApiOperation(value="그룹 탈퇴")
    @DeleteMapping("/group-member")
    public BooleanResponse deleteGroupMember(@RequestBody @Valid GroupIdMemberIdRequest request) {
        log.info("그룹 탈퇴 요청");
        Groups group = groupService.findGroupByGroupId(request.getGroupId());
        Member member = memberService.findById(request.getUserId());

        return new BooleanResponse(groupService.leaveGroupMember(group, member));
    }

    @ApiOperation(value="그룹 정보", notes="요청한 그룹의 정보를 출력합니다.\n" +
            "그룹정보와 그룹원들의 이번주의 기록을 반환.")
    @GetMapping("/{groupId}")
    public GroupInfoResponse getGroupInfo(@PathVariable Integer groupId) {
        log.info("그룹 정보 요청");
        log.info("Group ID : {}, 그룹 정보 요청", groupId);
        Groups group = groupService.findGroupByGroupId(groupId);
        List<Member> memberList = groupService.findAllMemberByGroup(group);
        List<GroupUserDataVo> groupWeeklyUserDataVoList = memberList.stream()
                .map(m -> new GroupUserDataVo(m, statisticsService.findWeeklyStatistics(m.getId(), Calendar.getInstance())))
                .collect(Collectors.toList());
        GroupWeeklyUserDataDto groupWeeklyUserDataDto = new GroupWeeklyUserDataDto(groupWeeklyUserDataVoList.size(), groupWeeklyUserDataVoList);
        double totalTime = groupWeeklyUserDataVoList.stream().mapToDouble(GroupUserDataVo::getTotalSeconds).sum();
        double totalDistance = groupWeeklyUserDataVoList.stream().mapToDouble(GroupUserDataVo::getDistance).sum();
        GroupWeeklyStatVo groupWeeklyStatVo = new GroupWeeklyStatVo(totalTime, totalDistance);
        log.info("Group ID : {}, 그룹 정보 불러오기 완료", groupId);
        return new GroupInfoResponse(group, groupWeeklyStatVo, groupWeeklyUserDataDto);
    }

    @ApiOperation(value = "모든 그룹원 요청", notes="요청한 그룹의 모든 그룹원들의 정보를 출력합니다.\n" +
            "아이디/이름/티어/사진\n" +
            "+ 당일 통계 량(거리, 시간, 칼로리)")
    @GetMapping("/group-member/{groupId}")
    public FindMembersResponseDto getGroupMember(@PathVariable Integer groupId) {
        log.info("Group ID : {}, 모든 그룹원 정보 요청", groupId);
        Groups group = groupService.findGroupByGroupId(groupId);
        List<Member> memberList = groupService.findAllMemberByGroup(group);
        List<GroupUserDataVo> groupUserDataVoList = memberList.stream()
                .map(m -> new GroupUserDataVo(m, statisticsService.findWeeklyStatistics(m.getId(), Calendar.getInstance())))
                .sorted(GroupUserDataVo.COMPARE_BY_NICKNAME)
                .collect(Collectors.toList());
        log.info("Group ID : {}, 모든 그룹원 정보 불러오기 완료", groupId);
        return new FindMembersResponseDto(groupUserDataVoList.size(), groupUserDataVoList);
    }

    @ApiOperation(value = "그룹장 위임", notes = "그룹장을 다른 그룹원에게 위임합니다.")
    @PutMapping("group-member/leader")
    public BooleanResponse modifyGroupLeader(@RequestBody @Valid ModifyGroupLeaderRequest request) {
        log.info("모든 그룹원 위임 요청", request.getGroupId(), request.getNewGroupLeaderUserId());
        Groups group = groupService.findGroupByGroupId(request.getGroupId());
        Member member = memberService.findById(request.getNewGroupLeaderUserId());
        return new BooleanResponse(groupService.changeGroupLeaderByGroupAndMember(group, member));
    }

    @ApiOperation(value = "그룹 삭제", notes = "그룹을 삭제합니다. 그룹 삭제는 그룹장만이 할 수 있다.")
    @DeleteMapping("{groupId}")
    public BooleanResponse deleteGroup(@PathVariable Integer groupId, @RequestParam Long userId) {
        log.info("ID : {}, 그룹 삭제 요청", userId);
        Member member = memberService.findById(userId);
        Groups group = groupService.findGroupByGroupId(groupId);
        Member leader = groupService.findGroupLeader(group);
        if (!member.equals(leader)){
            log.info("ID : {}, Group ID : {}, Group Leader ID : {}", userId, groupId, leader.getId());
            log.info("요청 유저({}) 와 그룹 리더({}) 불일치, 그룹 삭제 실패", userId, groupId, leader.getId());
            throw new ApiException(ExceptionEnum.GROUP_NOT_LEADER);
        }
        return new BooleanResponse(groupService.deleteGroup(group));
    }

    @ApiOperation(value = "그룹 정보 변경", notes = "그룹의 정보를 변경합니다.")
    @PutMapping("{groupId}")
    public BooleanResponse modifyGroupInfo(@PathVariable Integer groupId, @RequestBody @Valid ModifyGroupInfoRequest request) {
        log.info("Group ID : {}, 그룹 정보 변경 요청", groupId);
        Groups groups = groupService.findGroupByGroupId(groupId);
        return new BooleanResponse(groupService.modifyGroupInfo(groups, request));
    }

    @ApiOperation(value="중복 그룹명 확인")
    @GetMapping ("/validation-duplicate-name")
    public BooleanResponse validationDuplicateNickname(@RequestParam String target) {
        log.info("중복 그룹 이름 확인");
        return new BooleanResponse(!groupService.existsByNickname(target));
    }
}
