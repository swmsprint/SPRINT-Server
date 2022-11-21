package sprint.server.controller.datatransferobject.response;

import lombok.Data;
import sprint.server.domain.member.Member;

import java.util.Comparator;

@Data
public class GroupUserDataVo {
    private Long id;
    private String nickName;
    private int tierId;
    private String picture;
    private double distance;
    private double totalSeconds;
    private double energy;

    public GroupUserDataVo(Member member, StatisticsInfoVO statisticsInfoVO){
        this.id = member.getId();
        this.nickName = member.getNickname();
        this.tierId = member.getTierId();
        this.picture = member.getPicture();
        this.distance = statisticsInfoVO.getDistance();
        this.totalSeconds = statisticsInfoVO.getTotalSeconds();
        this.energy = statisticsInfoVO.getEnergy();
    }

    public static final Comparator<GroupUserDataVo> COMPARE_BY_NICKNAME = Comparator.comparing(o -> o.distance, Comparator.reverseOrder());
}
