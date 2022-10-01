package sprint.server.controller.datatransferobject.response;

import lombok.Data;
import sprint.server.domain.friend.FriendState;
import sprint.server.domain.member.Member;

import java.util.Comparator;
import java.util.List;

@Data
public class FindMembersResponseVo {
    private Long userId;
    private String nickname;
    private String email;
    private float height;
    private float weight;
    private int tierId;
    private String picture;
    private FriendState isFriend;

    public FindMembersResponseVo(Member member, List<Long> friendsList, List<Long> requestedFriendsList, List<Long> receivedFriendsList){
        this.userId = member.getId();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.height = member.getHeight();
        this.weight = member.getWeight();
        this.tierId = member.getTierId();
        this.picture = member.getPicture();
        this.isFriend = friendsList.contains(member.getId()) ? FriendState.ACCEPT :
                requestedFriendsList.contains(member.getId()) ? FriendState.REQUEST:
                receivedFriendsList.contains(member.getId()) ? FriendState.RECEIVE :
                FriendState.NOT_FRIEND;
    }

    public static final Comparator<FindMembersResponseVo> COMPARE_BY_NICKNAME = Comparator.comparing(o -> o.nickname);
}



