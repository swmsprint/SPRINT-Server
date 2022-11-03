package sprint.server.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.service.StatisticsService;
import sprint.server.service.UserMatchService;

@Slf4j
@Component
@RequiredArgsConstructor
public class statisticsScheduler {

    private final StatisticsService statisticsService;
    private final UserMatchService userMatchService;

//    매주 월요일 새벽 3시 에 전주 통계 저장
    @Scheduled(cron = "0 0 3 ? * MON", zone = "GMT+9:00")
//    @Scheduled(cron = "*/20 * * * * *")
    public void savePreviousWeekStatistics(){
        statisticsService.savePreviousStatistics(StatisticsType.Weekly);
        log.info("===Insert statistics Weekly finish===");
    }




    //매월 1일 새벽 4시에 그 전달 통계 저장
    @Scheduled(cron = "0 0 4 1 1/1 ?", zone = "GMT+9:00")
    public void savePreviousMonthStatistics(){
        statisticsService.savePreviousStatistics(StatisticsType.Monthly);
        log.info("===Insert statistics Monthly finish===");
    }


    //매년 1월 1일 새벽 5시에 그 전해 통계 저장
    @Scheduled(cron = "0 0 5 1 1 ?")
    public void savePreviousYearStatistics(){
        statisticsService.savePreviousStatistics(StatisticsType.Yearly);
        log.info("===Insert statistics Yearly finish===");
    }

    //매주 리그 매칭
    @Scheduled(cron = "0 0 4 ? * WED", zone = "GMT+9:00")
    public void matchingUser(){
        userMatchService.matchingApplyUser();
        log.info("===Matching League===");
    }


    //매주 리그 매칭
    @Scheduled(cron = "0 0 4 ? * WED", zone = "GMT+9:00")
    public void finishLeague(){
        userMatchService.matchingApplyUser();
        log.info("===Matching League===");
    }
}
