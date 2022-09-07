package sprint.server.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sprint.server.controller.datatransferobject.response.StatisticsInfoVO;
import sprint.server.domain.member.Member;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.StatisticsRepository;
import sprint.server.service.StatisticsService;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final StatisticsService statisticsService;
    private final StatisticsRepository statisticsRepository;
    private final MemberRepository memberRepository;
    @Scheduled(cron = "*/10 * * * * *")
    public void doCronJob() {
        System.out.println(Thread.currentThread() + ": doing a cron job at "+ new Date() + ".");
    }

    //매주 월요일 새벽 3시 에 전주 통계 저장
    @Scheduled(cron = "0 0 3 ? * MON")
    public void savePreviousWeekStatistics(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        List<Member> members = memberRepository.findAll();

        for(Member member: members) {
            StatisticsInfoVO statisticsInfoVO = statisticsService.findWeeklyStatistics(member.getId(),calendar);
            Statistics statistics = Statistics.builder()
                    .distance(statisticsInfoVO.getDistance())
                    .totalSeconds(statisticsInfoVO.getTotalSeconds())
                    .energy(statisticsInfoVO.getEnergy())
                    .time(new Timestamp(calendar.getTimeInMillis()))
                    .build();
            statisticsRepository.save(statistics);
        }

        log.info("Insert statistics Weekly finish");
    }
    //매월 1일 새벽 4시에 그 전달 통계 저장
    @Scheduled(cron = "0 0 4 1 1/1 ?")
    public void savePreviousMonthStatistics(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        List<Member> members = memberRepository.findAll();

        for(Member member: members) {
            StatisticsInfoVO statisticsInfoVO = statisticsService.findMonthlyStatistics(member.getId(),calendar);
            Statistics statistics = Statistics.builder()
                    .distance(statisticsInfoVO.getDistance())
                    .totalSeconds(statisticsInfoVO.getTotalSeconds())
                    .energy(statisticsInfoVO.getEnergy())
                    .time(new Timestamp(calendar.getTimeInMillis()))
                    .build();
            statisticsRepository.save(statistics);
        }

        log.info("Insert statistics Monthly finish");
    }


    //매년 1월 1일 새벽 5시에 그 전해 통계 저장
    @Scheduled(cron = "0 0 5 1 1 ?")
    public void savePreviousYearStatistics(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        List<Member> members = memberRepository.findAll();

        for(Member member: members) {
            StatisticsInfoVO statisticsInfoVO = statisticsService.findYearlyStatistics(member.getId(),calendar);
            Statistics statistics = Statistics.builder()
                    .distance(statisticsInfoVO.getDistance())
                    .totalSeconds(statisticsInfoVO.getTotalSeconds())
                    .energy(statisticsInfoVO.getEnergy())
                    .time(new Timestamp(calendar.getTimeInMillis()))
                    .build();
            statisticsRepository.save(statistics);
        }

        log.info("Insert statistics Yearly finish");
    }


}
