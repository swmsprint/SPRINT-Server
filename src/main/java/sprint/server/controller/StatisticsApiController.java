package sprint.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.service.MemberService;
import sprint.server.service.StatisticsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatisticsApiController {

    private final StatisticsService statisticsService;
    private final MemberService memberService;

    @GetMapping("/api/statistics/join/{id}")
    public long createStatistics(@PathVariable("id")long memberId){
        return statisticsService.join(memberId);
    }

    @GetMapping("/api/statistics/{id}")
    public List<Statistics> viewStatisticsDetail(@PathVariable("id")Long memberID){
        return statisticsService.findByStatisticsTypeAndMember_Id(StatisticsType.Daily,memberID);
    }
}
