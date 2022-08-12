package sprint.server.controller.datatransferobject.response;

import lombok.Data;
import sprint.server.domain.member.Member;

import java.util.Comparator;

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

    public static Comparator<LoadMembersResponseDto> COMPARE_BY_NICKNAME = Comparator.comparing(o -> o.nickName);
}



