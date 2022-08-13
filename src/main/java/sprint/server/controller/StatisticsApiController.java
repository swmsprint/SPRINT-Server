package sprint.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.controller.datatransferobject.StatisticsDTO;
import sprint.server.controller.datatransferobject.response.ViewStatisticsResponse;
import sprint.server.service.StatisticsService;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StatisticsApiController {

    private final StatisticsService statisticsService;


    @GetMapping("/api/statistics/{id}")
    public ViewStatisticsResponse viewStatisticsDetail(@PathVariable("id")Long memberID){
        Calendar calendar = Calendar.getInstance();
        return new ViewStatisticsResponse(
                statisticsService.findDailyStatistics(memberID,calendar),
                statisticsService.findWeeklyStatistics(memberID,calendar),
                statisticsService.findMonthlyStatistics(memberID,calendar),
                statisticsService.findYearlyStatistics(memberID,calendar),
                statisticsService.findTotalStatistics(memberID));
    }


    /**
     * 특정 달의 스트릭을 반환하는 함수
     * @param memberID
     * @return List 형식의 스트릭
     */
    @GetMapping("/api/statistics/streak/{id}")
    public List<Double> viewStreakDetail(@PathVariable("id")Long memberID){
        return statisticsService.findMonthlyStreak(memberID, Calendar.getInstance());
    }

}
