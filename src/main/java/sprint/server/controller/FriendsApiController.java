package sprint.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.controller.datatransferobject.request.CreateFriendsRequest;
import sprint.server.controller.datatransferobject.response.CreateFriendsResponse;
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
}
