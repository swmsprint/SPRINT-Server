package sprint.server.controller.datatransferobject.response;

import lombok.Data;
import sprint.server.domain.member.Member;

@Data
public class FindBlockResponseVo {
    private Long userId;
    private String nickname;
    private String picture;

    public FindBlockResponseVo(Member member){
        this.userId = member.getId();
        this.nickname = member.getNickname();
        this.picture = member.getPicture();
    }
}
