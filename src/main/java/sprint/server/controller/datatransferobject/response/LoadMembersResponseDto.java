package sprint.server.controller.datatransferobject.response;

import lombok.Data;
import sprint.server.domain.Member.Member;

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

    public static Comparator<LoadMembersResponseDto> COMPARE_BY_NICKNAME = new Comparator<LoadMembersResponseDto>() {
        @Override
        public int compare(LoadMembersResponseDto o1, LoadMembersResponseDto o2) {
            return o1.nickName.compareTo(o2.nickName);
        }
    };
}



