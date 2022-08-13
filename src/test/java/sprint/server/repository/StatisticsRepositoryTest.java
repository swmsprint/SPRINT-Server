package sprint.server.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import sprint.server.domain.Member;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SpringBootTest
@Rollback(value = false)
class StatisticsRepositoryTest {

    @Autowired StatisticsRepository statisticsRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    void 통계레포지토리_Between_테스트() {

        //Given
        Member member = new Member();
        member.setName("plz");
        memberRepository.save(member);

        Statistics statistics = new Statistics();
        statistics.setMember(memberRepository.findByName("plz"));
        statistics.setTime(Timestamp.valueOf("2022-08-02 07:48:29.391"));
        statistics.setStatisticsType(StatisticsType.Daily);
        statisticsRepository.save(statistics);

        //When
        Statistics statisticsFind = statisticsRepository.findByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Daily,memberRepository.findByName("plz").getId(),Timestamp.valueOf("2021-08-01 00:00:00.0"),Timestamp.valueOf("2021-08-07 00:00:00.0"));

        //Then
        Assertions.assertEquals(null,statisticsFind);
//        Assertions.assertEquals(statistics.getMember().getName(),statisticsFind.getMember().getName());
    }

    @Test
    void 시간테스트(){
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        calendar1.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        calendar1.add(Calendar.DATE, 7);
        calendar1.set(Calendar.AM_PM,Calendar.PM);
        calendar1.set(Calendar.HOUR,11);
        calendar1.set(Calendar.MINUTE,59);
        calendar1.set(Calendar.SECOND,59);
        calendar1.set(Calendar.MILLISECOND,999);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);

        calendar.set(Calendar.AM_PM,Calendar.AM);
        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);


        System.out.println(dateFormat.format(calendar1.getTime()));
        System.out.println(dateFormat.format(calendar.getTime()));
    }



}