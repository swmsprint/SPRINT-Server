package sprint.server.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.service.StatisticsService;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final StatisticsService statisticsService;
    @Scheduled(cron = "*/10 * * * * *")
    public void doCronJob() {
        System.out.println(Thread.currentThread() + ": doing a cron job at "+ new Date() + ".");
    }

    //매주 새벽 일요일에 그 다음주 통계 만듬
    @Scheduled(cron = "0 0 4 ? * SUN")
    public void createNextWeekStatistics(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE,1);
//        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        Timestamp nextTime= Timestamp.valueOf(dateFormat.format(calendar));

        log.info("test");
    }
    //매월 28 새벽 4시에 그 다음달 통계 만듬
    @Scheduled(cron = "0 0 4 28 1/1 ?")
    public void createNextMonthStatistics(){
        System.out.println("hi~");
        log.info("test");
    }


    //매년 마지막날 새벽 4시에 그 다음해 통계 만듬
    @Scheduled(cron = "0 0 4 31 12 ?")
    public void createNextYearStatistics(){
        System.out.println("hi~");
        log.info("test");
    }


}
