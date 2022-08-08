package sprint.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.*;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.domain.Member;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.FriendsRepository;
import sprint.server.service.FriendsService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FriendsApiController {
    private final FriendsService friendsService;
    private final FriendsRepository friendsRepository;
    @PostMapping("/api/friends")
    public CreateFriendsResponse createFriends(@RequestBody @Valid CreateFriendsRequest request) {
        Friends friends = friendsService.FriendsRequest(request.getSourceUserId(), request.getTargetUserId());
        return new CreateFriendsResponse(friendsRepository.existsById(friends.getId()));
    }

    @PostMapping("/api/friends/accept")
    public CreateFriendsResultResponse AcceptFriends(@RequestBody @Valid CreateFriendsResultRequest request) {
        return new CreateFriendsResultResponse(friendsService.AcceptFriendsRequest(request.getSourceUserId(), request.getTargetUserId()));
    }

    @PutMapping("/api/friends/reject")
    public CreateFriendsResultResponse RejectFriends(@RequestBody @Valid CreateFriendsResultRequest request) {
        return new CreateFriendsResultResponse(friendsService.RejectFriendsRequest(request.getSourceUserId(), request.getTargetUserId()));
    }

    @PutMapping("/api/friends/delete")
    public DeleteFriendsResponse DeleteFriends(@RequestBody @Valid DeleteFriendsRequest request) {
        return new DeleteFriendsResponse(friendsService.DeleteFriends(request.getSourceUserId(), request.getTargetUserId()));
    }

    @PutMapping("api/friends/cancel")
    public CancelFriendsResponse CancelFriendsRequest(@RequestBody @Valid CancelFriendsRequest request){
        return new CancelFriendsResponse(friendsService.CancelFriends(request.getSourceUserId(), request.getTargetUserId()));
    }


    /**
     * 나의 친구 목록
     * @param request -> userId
     * @return
     */
    @GetMapping("/api/friends/list/myfriends")
    public LoadFriendsResponse<LoadFriendsResponseDto> LoadFriendsFriends(@RequestBody @Valid LoadFriendsRequest request) {
        List<Member> members = friendsService.LoadFriendsBySourceMember(request.getUserId(), FriendState.ACCEPT);
        List<LoadFriendsResponseDto> result = members.stream()
                .map(member -> new LoadFriendsResponseDto(member))
                .collect(Collectors.toList());
        return new LoadFriendsResponse(result.size(), result);
    }

    /**
     * 내가 받은 친구 요청 목록
     * @param request -> userId
     * @return
     */
    @GetMapping("/api/friends/list/receive")
    public LoadFriendsResponse<LoadFriendsResponseDto> LoadFriendsReceive(@RequestBody @Valid LoadFriendsRequest request) {
        List<Member> members = friendsService.LoadFriendsByTargetMember(request.getUserId(), FriendState.REQUEST);
        List<LoadFriendsResponseDto> result = members.stream()
                .map(member -> new LoadFriendsResponseDto(member))
                .collect(Collectors.toList());
        return new LoadFriendsResponse(result.size(), result);
    }

    /**
     * 내가 보낸 친구 요청 목록
     * @param request -> userId
     * @return
     */
    @GetMapping("/api/friends/list/request")
    public LoadFriendsResponse<LoadFriendsResponseDto> LoadFriendsRequest(@RequestBody @Valid LoadFriendsRequest request) {
        List<Member> members = friendsService.LoadFriendsBySourceMember(request.getUserId(), FriendState.REQUEST);
        List<LoadFriendsResponseDto> result = members.stream()
                .map(member -> new LoadFriendsResponseDto(member))
                .collect(Collectors.toList());
        return new LoadFriendsResponse(result.size(), result);
    }
}
