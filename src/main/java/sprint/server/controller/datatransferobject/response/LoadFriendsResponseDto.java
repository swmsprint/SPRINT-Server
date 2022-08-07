package sprint.server.controller.datatransferobject.response;

import lombok.Data;
import sprint.server.domain.Member;


@Data
public class LoadFriendsResponseDto {
    private Long userId;
    private String name;
    private String email;
    private float height;
    private float weight;
    private int tierId;
    private String picture;

    public LoadFriendsResponseDto(Member member){
        this.userId = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.height = member.getHeight();
        this.weight = member.getWeight();
        this.tierId = member.getTierId();
        this.picture = member.getPicture();
    }
}



