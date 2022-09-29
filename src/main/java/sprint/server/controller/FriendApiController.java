package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.*;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.member.Member;
import sprint.server.domain.friend.FriendState;
import sprint.server.service.FriendService;
import sprint.server.service.MemberService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user-management/friend")
public class FriendApiController {
    private final FriendService friendService;
    private final MemberService memberService;

    @ApiOperation(value="친구추가 요청", notes =
            "sourceUserId -> 친구추가 요청을 보내는 유저\ntargetUserId -> 친구추가 요청을 받는 유저")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("")
    public BooleanResponse createFriends(@RequestBody @Valid FriendsRequest request) {
        return new BooleanResponse(friendService.requestFriends(request.getSourceUserId(), request.getTargetUserId()));
    }

    @ApiOperation(value="친구 수락/거절/취소", notes =
    "수락: ACCEPT\n거절: REJECT\n취소: CANCEL")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PutMapping("")
    public BooleanResponse modifyFriendsState(@RequestBody @Valid ModifyFriendsRequest request) {
        switch (request.getFriendState()) {
            case ACCEPT:
                return new BooleanResponse(friendService.acceptFriendsRequest(request.getTargetUserId(), request.getSourceUserId()));
            case REJECT:
                return new BooleanResponse(friendService.rejectFriendsRequest(request.getTargetUserId(), request.getSourceUserId()));
            case CANCEL:
                return new BooleanResponse(friendService.cancelFriends(request.getSourceUserId(), request.getTargetUserId()));
            default:
                throw new ApiException(ExceptionEnum.FRIENDS_METHOD_NOT_FOUND);
        }
    }

    @ApiOperation(value = "친구 제거")
    @DeleteMapping("")
    public BooleanResponse deleteFriends(@RequestBody @Valid FriendsRequest request) {
        Member sourceMember = memberService.findById(request.getSourceUserId());
        Member targetMember = memberService.findById(request.getTargetUserId());
        return new BooleanResponse(friendService.deleteFriends(sourceMember, targetMember));
    }
    @ApiOperation(value="친구 목록 요청",
            notes = "Example: http://localhost:8080/api/user-management/friends/3")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/{userId}")
    public FindMembersResponseDto<FindFriendsResponseVo> findFriends(@PathVariable Long userId) {
        List<Member> members = friendService.findFriendsByMemberId(userId, FriendState.ACCEPT);
        List<FindFriendsResponseVo> result = members.stream()
                .map(FindFriendsResponseVo::new)
                .sorted(FindFriendsResponseVo.COMPARE_BY_NICKNAME)
                .collect(Collectors.toList());
        return new FindMembersResponseDto(result.size(), result);
    }

    @ApiOperation(value="사용자가 받은 친구 추가 요청 목록",
            notes = "Example: http://localhost:8080/api/user-management/friends/3/received")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/{userId}/received")
    public FindMembersResponseDto<FindFriendsResponseVo> findFriendsReceive(@PathVariable Long userId) {
        List<Member> members = friendService.findByTargetMemberIdAndFriendState(userId, FriendState.REQUEST);
        List<FindFriendsResponseVo> result = members.stream()
                .map(FindFriendsResponseVo::new)
                .collect(Collectors.toList());
        return new FindMembersResponseDto(result.size(), result);
    }

    @ApiOperation(value="사용자가 보낸 친구 추가 요청 목록",
            notes = "Example: http://localhost:8080/api/user-management/friends/3/requested")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/{userId}/requested")
    public FindMembersResponseDto<FindFriendsResponseVo> findFriendsRequest(@PathVariable Long userId) {
        List<Member> members = friendService.findBySourceMemberIdAndFriendState(userId, FriendState.REQUEST);
        List<FindFriendsResponseVo> result = members.stream()
                .map(FindFriendsResponseVo::new)
                .collect(Collectors.toList());
        return new FindMembersResponseDto(result.size(), result);
    }
}
