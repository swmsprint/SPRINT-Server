package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.response.ViewStatisticsResponse;
import sprint.server.service.StatisticsService;

import java.util.Calendar;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics/")
public class StatisticsApiController {

    private final StatisticsService statisticsService;

    @ApiOperation(value="통계 정보 반환", notes = "조회를 요청하는 날짜에 해당하는 전체 통계정보를 반환합니다")
    @GetMapping("{id}")
    public ViewStatisticsResponse viewStatisticsDetail(@PathVariable("id")Long memberID){
        Calendar calendar = Calendar.getInstance();
        return new ViewStatisticsResponse(
                statisticsService.findDailyStatistics(memberID,calendar),
                statisticsService.findWeeklyStatistics(memberID,calendar),
                statisticsService.findMonthlyStatistics(memberID,calendar),
                statisticsService.findYearlyStatistics(memberID,calendar),
                statisticsService.findTotalStatistics(memberID,calendar));
    }


    /**
     * 특정 달의 스트릭을 반환하는 함수
     * @param memberID
     * @return List 형식의 스트릭
     */
    @GetMapping("streak/{id}")
    public List<Double> viewStreakDetail(@PathVariable("id")Long memberID, @RequestParam("year")int year, @RequestParam("month")int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.YEAR,year);
        return statisticsService.findMonthlyStreak(memberID, calendar);
    }

}
