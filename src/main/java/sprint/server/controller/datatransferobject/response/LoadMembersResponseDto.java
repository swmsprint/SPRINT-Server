package sprint.server.controller.datatransferobject.response;

import lombok.Data;
import sprint.server.domain.Member.Member;

@Data
public class LoadMembersResponseDto {
    private Long userId;
    private String nickName;
    private String email;
    private float height;
    private float weight;
    private int tierId;
    private String picture;

    public LoadMembersResponseDto(Member member){
        this.userId = member.getId();
        this.nickName = member.getNickname();
        this.email = member.getEmail();
        this.height = member.getHeight();
        this.weight = member.getWeight();
        this.tierId = member.getTierId();
        this.picture = member.getPicture();
    }
}



