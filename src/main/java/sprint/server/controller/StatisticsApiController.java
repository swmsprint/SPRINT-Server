package sprint.server.controller;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.response.ViewStatisticsResponse;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.member.Member;
import sprint.server.service.MemberService;

import sprint.server.service.StatisticsService;


import java.util.Calendar;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics/")
public class StatisticsApiController {

    private final StatisticsService statisticsService;
    private final MemberService memberService;

    @ApiOperation(value="통계 정보 반환", notes = "조회를 요청하는 날짜에 해당하는 전체 통계정보를 반환합니다")
    @GetMapping("{id}")
    public ViewStatisticsResponse viewStatisticsDetail(@PathVariable("id")Long memberID){

        Member member = memberService.findById(memberID);

        Calendar calendar = Calendar.getInstance();
        return new ViewStatisticsResponse(
                statisticsService.findDailyStatistics(member.getId(),calendar),
                statisticsService.findWeeklyStatistics(member.getId(),calendar),
                statisticsService.findMonthlyStatistics(member.getId(),calendar),
                statisticsService.findYearlyStatistics(member.getId(),calendar),
                statisticsService.findTotalStatistics(member.getId(),calendar));
    }


    /**
     * 특정 달의 스트릭을 반환하는 함수
     * @param memberID
     * @return List 형식의 스트릭
     */
    @GetMapping("streak/{id}")
    public List<Double> viewStreakDetail(@PathVariable("id")Long memberID, @RequestParam("year")int year, @RequestParam("month")int month){

        if(year<0 || month<0 || month>12){
            throw new ApiException(ExceptionEnum.DATE_FORMAT_ERROR);
        }

        Member member = memberService.findById(memberID);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.YEAR,year);
        return statisticsService.findMonthlyStreak(member.getId(), calendar);
    }

}
