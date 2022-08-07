package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import sprint.server.domain.Member;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.MemberRepository;

import java.util.List;


@Data
public class LoadFriendsResponse {
    private Long userId;
    private String name;
    private String email;
    private float height;
    private float weight;
    private int tierId;
    private String picture;

    public LoadFriendsResponse(Member member){
        this.userId = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.height = member.getHeight();
        this.weight = member.getWeight();
        this.tierId = member.getTierId();
        this.picture = member.getPicture();
    }
}



