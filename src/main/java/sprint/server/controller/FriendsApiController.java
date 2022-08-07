package sprint.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.CreateFriendsResultRequest;
import sprint.server.controller.datatransferobject.request.CreateFriendsRequest;
import sprint.server.controller.datatransferobject.request.DeleteFriendsRequest;
import sprint.server.controller.datatransferobject.response.CreateFriendsResultResponse;
import sprint.server.controller.datatransferobject.response.CreateFriendsResponse;
import sprint.server.controller.datatransferobject.response.DeleteFriendsResponse;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.FriendsRepository;
import sprint.server.service.FriendsService;

import javax.validation.Valid;

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

}
