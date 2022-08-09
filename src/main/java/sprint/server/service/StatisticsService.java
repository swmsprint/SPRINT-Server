package sprint.server.service;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Member;
import sprint.server.domain.Running;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.RunningRepository;
import sprint.server.repository.StatisticsRepository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final MemberRepository memberRepository;
    private final RunningRepository runningRepository;
    private final StatisticsRepository statisticsRepository;


    @Transactional
    public List<Statistics> findByStatisticsTypeAndMember_Id(StatisticsType statisticsType, long memberId) {
        return statisticsRepository.findByStatisticsTypeAndMember_Id(statisticsType ,memberId);
    }


    @Transactional
    public long join(long memberId) {
        Member member = memberRepository.findById(memberId).get();

        Statistics statistics = new Statistics();
        statistics.setStatisticsType(StatisticsType.Daily);
        statistics.setMember(member);
        statisticsRepository.save(statistics);
        return statistics.getId();
    }

    /**
     * 러닝 종료 후 통계를 업데이트 해준다
     */
    @Transactional
    public void updateStatistics(Running running,StatisticsType statisticsType) {
        Member member = running.getMember();

        //러닝에 해당하는 주/월 첫날 정각시간 설정
        Timestamp timeStart= Timestamp.valueOf(getCalendarStart(running, statisticsType));

        //러닝에 해당하는 주/월 마지막날 밤 11:59:59:999 설정
        Timestamp timeEnd= Timestamp.valueOf(getCalendarEnd(running, statisticsType));

        //그 이후 생성된 ststistics를 찾는다
        Statistics findStatistics = statisticsRepository.findByStatisticsTypeAndMember_IdAndTimeBetween(
                statisticsType, memberRepository.findById(member.getId()).get().getId(), timeStart, timeEnd);


        //만약 존재하지 않으면
        if(findStatistics == null) {
            long id = createStatistics(member,running,statisticsType);
            findStatistics = statisticsRepository.findById(id).get();
        }
        findStatistics.setDistance(findStatistics.getDistance()+running.getDistance());
        findStatistics.setCount(findStatistics.getCount()+1);
        findStatistics.setTotalSeconds(findStatistics.getTotalSeconds()+ running.getDuration());


    }

    private String getCalendarEnd(Running running, StatisticsType statisticsType) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(running.getStartTime());
        if(statisticsType == StatisticsType.Weekly) {
            calendarEnd.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            calendarEnd.add(Calendar.DATE, 7);
        }
        else if(statisticsType == StatisticsType.Monthly){calendarEnd.set(Calendar.DAY_OF_MONTH,calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH));}

        calendarEnd.set(Calendar.AM_PM,Calendar.PM);
        calendarEnd.set(Calendar.HOUR,11);
        calendarEnd.set(Calendar.MINUTE,59);
        calendarEnd.set(Calendar.SECOND,59);
        calendarEnd.set(Calendar.MILLISECOND,999);
        return dateFormat.format(calendarEnd.getTime());
    }

    private String getCalendarStart(Running running, StatisticsType statisticsType) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(running.getStartTime());
        if(statisticsType == StatisticsType.Weekly) calendarStart.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        else if(statisticsType == StatisticsType.Monthly) calendarStart.set(Calendar.DAY_OF_MONTH,1);

        calendarStart.set(Calendar.AM_PM,Calendar.AM);
        calendarStart.set(Calendar.HOUR,0);
        calendarStart.set(Calendar.MINUTE,0);
        calendarStart.set(Calendar.SECOND,0);
        calendarStart.set(Calendar.MILLISECOND,0);
        return dateFormat.format(calendarStart.getTime());
    }

    private Long createStatistics(Member member, Running running, StatisticsType statisticsType) {
        Statistics statistics = new Statistics();
        statistics.setMember(member);
        statistics.setTime(running.getStartTime());
        statistics.setStatisticsType(statisticsType);
        statisticsRepository.save(statistics);

        return statistics.getId();
    }

    /**
     * 러닝 종료 후 통계를 업데이트 해준다
     */
    @Transactional
    public long makeDailyStatistics(Member member){
        Statistics statistics = new Statistics();
        statistics.setMember(member);
        statistics.setTime(Timestamp.valueOf(LocalDateTime.now()));
        statistics.setStatisticsType(StatisticsType.Daily);
        statisticsRepository.save(statistics);
        return 2;
    }


    @Transactional
    public void makeWeeklyStatistics(){
        System.out.println("hi~");
    }

    @Transactional
    public void makeMonthlyStatistics(){
        System.out.println("hi~");
    }


    /**
     * Daily, Weekly, Monthly 시간별로 파라미터를 만들어야함.
     */

    /**
     *  MemberId + (Daily, Weekly, Monthly) + (시간) 로 데이터롤 요청하는
     */
}
