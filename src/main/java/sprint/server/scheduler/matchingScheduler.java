package sprint.server.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sprint.server.service.UserMatchService;

import java.sql.Timestamp;
import java.util.Calendar;

@Slf4j
@Component
@RequiredArgsConstructor
public class matchingScheduler {
    private final UserMatchService userMatchService;
    //매주 리그 매칭
    @Scheduled(cron = "0 0 4 ? * WED", zone = "GMT+9:00")
    public void matchingUser(){
        userMatchService.matchingApplyUser();
        log.info("===Matching League===");
    }


    //매주 리그 종료
    @Scheduled(cron = "0 0 1 ? * MON", zone = "GMT+9:00")
    public void finishLeague(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        userMatchService.finishLeague(new Timestamp(calendar.getTimeInMillis()));
        log.info("===Finish League and Save Information===");
    }


}
