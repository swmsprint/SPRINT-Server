package sprint.server.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import sprint.server.domain.Member;
import sprint.server.domain.Running;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.RunningRepository;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Rollback(value = false)
class StatisticsServiceTest {

    @Autowired StatisticsService statisticsService;
    @Autowired RunningRepository runningRepository;
    @Autowired MemberRepository memberRepository;
            ;

    @Test
    void updateStatistics() {
    }

    @Test
    void makeDailyStatistics() {
    }

    @Test
    void 기본_MonthlyStreak_테스트() {

        //Given

        Member member = new Member();
        member.setName("yewon");
        memberRepository.save(member);

        Running running = new Running();
        running.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        running.setDistance(12.323);
        running.setEnergy(213);
        running.setWeight(80);
        running.setMember(memberRepository.findByName("yewon"));
        runningRepository.save(running);

        statisticsService.updateStatistics(running, StatisticsType.Daily);
        statisticsService.updateStatistics(running, StatisticsType.Weekly);
        statisticsService.updateStatistics(running, StatisticsType.Monthly);

        //When
        List<Double> result = statisticsService.findMonthlyStreak(member.getId(), Calendar.getInstance());


        //Then
        System.out.println(result.toString());
    }

    @Test
    void 특정달_MonthlyStreak_테스트() {

        //Given

        Member member = new Member();
        member.setName("yewon");
        memberRepository.save(member);

        Running running = new Running();
        running.setStartTime(Timestamp.valueOf("2021-08-02 07:48:29.391"));
        running.setDistance(12.32);
        running.setEnergy(213);
        running.setWeight(80);
        running.setMember(memberRepository.findByName("yewon"));
        runningRepository.save(running);

        statisticsService.updateStatistics(running, StatisticsType.Daily);
        statisticsService.updateStatistics(running, StatisticsType.Weekly);
        statisticsService.updateStatistics(running, StatisticsType.Monthly);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Timestamp.valueOf("2021-08-02 07:48:29.391"));

        //When
        List<Double> result = statisticsService.findMonthlyStreak(member.getId(), calendar);


        //Then
        System.out.println(result.toString());
    }


}