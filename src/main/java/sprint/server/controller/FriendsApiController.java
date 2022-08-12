package sprint.server.controller;

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
public class FriendsApiController {
    private final FriendsService friendsService;

    @PostMapping("/api/friends")
    public BooleanResponse createFriends(@RequestBody @Valid TwoMemberRequest request) {
        Friends friends = friendsService.requestFriends(request.getSourceUserId(), request.getTargetUserId());
        return new BooleanResponse(friendsService.existsById(friends.getId()));
    }

    @PostMapping("/api/friends/accept")
    public BooleanResponse acceptFriends(@RequestBody @Valid TwoMemberRequest request) {
        return new BooleanResponse(friendsService.acceptFriendsRequest(request.getTargetUserId(), request.getSourceUserId()));
    }

    @PutMapping("/api/friends/reject")
    public BooleanResponse rejectFriends(@RequestBody @Valid TwoMemberRequest request) {
        return new BooleanResponse(friendsService.rejectFriendsRequest(request.getTargetUserId(), request.getSourceUserId()));
    }

    @PutMapping("/api/friends/delete")
    public BooleanResponse deleteFriends(@RequestBody @Valid TwoMemberRequest request) {
        return new BooleanResponse(friendsService.deleteFriends(request.getSourceUserId(), request.getTargetUserId()));
    }

    @PutMapping("api/friends/cancel")
    public BooleanResponse cancelFriendsRequest(@RequestBody @Valid TwoMemberRequest request){
        return new BooleanResponse(friendsService.cancelFriends(request.getSourceUserId(), request.getTargetUserId()));
    }


    /**
     * 나의 친구 목록
     * @param request -> userId
     * @return number of friends list, friends list
     */
    @GetMapping("/api/friends/list/myfriends")
    public LoadMembersResponse<LoadMembersResponseDto> loadFriendsFriends(@RequestBody @Valid OneMemberRequest request) {
        List<Member> members = friendsService.loadFriendsBySourceMember(request.getUserId(), FriendState.ACCEPT);
        List<LoadMembersResponseDto> result = members.stream()
                .map(LoadMembersResponseDto::new)
                .sorted(LoadMembersResponseDto.COMPARE_BY_NICKNAME)
                .collect(Collectors.toList());
        return new LoadMembersResponse(result.size(), result);
    }

    /**
     * 내가 받은 친구 요청 목록
     * @param request -> userId
     * @return number of list, list
     */
    @GetMapping("/api/friends/list/receive")
    public LoadMembersResponse<LoadMembersResponseDto> loadFriendsReceive(@RequestBody @Valid OneMemberRequest request) {
        List<Member> members = friendsService.loadFriendsByTargetMember(request.getUserId(), FriendState.REQUEST);
        List<LoadMembersResponseDto> result = members.stream()
                .map(LoadMembersResponseDto::new)
                .collect(Collectors.toList());
        return new LoadMembersResponse(result.size(), result);
    }

    /**
     * 내가 보낸 친구 요청 목록
     * @param request -> userId
     * @return number of list, list
     */
    @GetMapping("/api/friends/list/request")
    public LoadMembersResponse<LoadMembersResponseDto> loadFriendsRequest(@RequestBody @Valid OneMemberRequest request) {
        List<Member> members = friendsService.loadFriendsBySourceMember(request.getUserId(), FriendState.REQUEST);
        List<LoadMembersResponseDto> result = members.stream()
                .map(LoadMembersResponseDto::new)
                .collect(Collectors.toList());
        return new LoadMembersResponse(result.size(), result);
    }
}
