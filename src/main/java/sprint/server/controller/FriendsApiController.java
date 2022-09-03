package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.*;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.domain.member.Member;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;
import sprint.server.service.FriendsService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends/")
public class FriendsApiController {
    private final FriendsService friendsService;

    @ApiOperation(value="친구추가 요청", notes =
            "sourceUserId -> 친구추가 요청을 보내는 유저\ntargetUserId -> 친구추가 요청을 받는 유저")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("request")
    public BooleanResponse createFriends(@RequestBody @Valid TwoMemberRequest request) {
        Friends friends = friendsService.requestFriends(request.getSourceUserId(), request.getTargetUserId());
        return new BooleanResponse(friendsService.existsById(friends.getId()));
    }

    @ApiOperation(value="친구추가 요청 수락", notes =
            "sourceUserId -> 친구추가 요청을 수락하는 유저\ntargetUserId -> 친구추가 요청을 보낸 유저")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("accept")
    public BooleanResponse acceptFriends(@RequestBody @Valid TwoMemberRequest request) {
        return new BooleanResponse(friendsService.acceptFriendsRequest(request.getTargetUserId(), request.getSourceUserId()));
    }

    @ApiOperation(value="친구추가 요청 거절", notes =
            "sourceUserId -> 친구추가 요청을 거절하는 유저\ntargetUserId -> 친구추가 요청을 보낸 유저")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PutMapping("reject")
    public BooleanResponse rejectFriends(@RequestBody @Valid TwoMemberRequest request) {
        return new BooleanResponse(friendsService.rejectFriendsRequest(request.getTargetUserId(), request.getSourceUserId()));
    }

    @ApiOperation(value="친구 제거", notes =
            "sourceUserId -> 친구제거 요청을 보내는 유저\ntargetUserId -> 친구목록에서 제거될 유저")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PutMapping("delete")
    public BooleanResponse deleteFriends(@RequestBody @Valid TwoMemberRequest request) {
        return new BooleanResponse(friendsService.deleteFriends(request.getSourceUserId(), request.getTargetUserId()));
    }

    @ApiOperation(value="친구 추가 요청 취소", notes =
            "sourceUserId -> 친구추가 요청을 보낸 유저\ntargetUserId -> 친구추가 요청을 받은 유저")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PutMapping("cancel")
    public BooleanResponse cancelFriendsRequest(@RequestBody @Valid TwoMemberRequest request){
        return new BooleanResponse(friendsService.cancelFriends(request.getSourceUserId(), request.getTargetUserId()));
    }


    @ApiOperation(value="친구 목록 요청", notes = "Example: http://localhost:8080/api/friends/list?userId=3")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("list")
    public FindMembersResponseDto<FindMembersResponseVo> findFriends(@RequestParam Long userId) {
        List<Member> members = friendsService.loadFriendsBySourceMember(userId, FriendState.ACCEPT);
        List<FindMembersResponseVo> result = members.stream()
                .map(FindMembersResponseVo::new)
                .sorted(FindMembersResponseVo.COMPARE_BY_NICKNAME)
                .collect(Collectors.toList());
        return new FindMembersResponseDto(result.size(), result);
    }

    @ApiOperation(value="사용자가 받은 친구 추가 요청 목록", notes = "Example: http://localhost:8080/api/friends/list/received?userId=3")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("list/received")
    public FindMembersResponseDto<FindMembersResponseVo> findFriendsReceive(@RequestParam Long userId) {
        List<Member> members = friendsService.loadFriendsByTargetMember(userId, FriendState.REQUEST);
        List<FindMembersResponseVo> result = members.stream()
                .map(FindMembersResponseVo::new)
                .collect(Collectors.toList());
        return new FindMembersResponseDto(result.size(), result);
    }

    @ApiOperation(value="사용자가 보낸 친구 추가 요청 목록", notes = "Example: http://localhost:8080/api/friends/list/requested?userId=3")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("list/requested")
    public FindMembersResponseDto<FindMembersResponseVo> findFriendsRequest(@RequestParam Long userId) {
        List<Member> members = friendsService.loadFriendsBySourceMember(userId, FriendState.REQUEST);
        List<FindMembersResponseVo> result = members.stream()
                .map(FindMembersResponseVo::new)
                .collect(Collectors.toList());
        return new FindMembersResponseDto(result.size(), result);
    }
}
