package sprint.server.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.StatisticsDTO;
import sprint.server.domain.Member;
import sprint.server.domain.Running;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.StatisticsRepository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final MemberRepository memberRepository;
    private final StatisticsRepository statisticsRepository;


    /**
     * 러닝 종료 후 통계를 업데이트 해준다
     */
    @Transactional
    public void updateStatistics(Running running,StatisticsType statisticsType) {
        Member member = running.getMember();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //러닝에 해당하는 주/월 첫날 정각시간 설정
        Timestamp timeStart= Timestamp.valueOf(dateFormat.format(getCalendarStart(running.getStartTime(), statisticsType).getTime()));

        //러닝에 해당하는 주/월 마지막날 밤 11:59:59:999 설정
        Timestamp timeEnd= Timestamp.valueOf(dateFormat.format(getCalendarEnd(running.getStartTime(), statisticsType).getTime()));

        //그 이후 생성된 statistics를 찾는다
        Statistics findStatistics = statisticsRepository.findByStatisticsTypeAndMemberIdAndTimeBetween(
                statisticsType, memberRepository.findById(member.getId()).get().getId(), timeStart, timeEnd);


        //만약 존재하지 않으면
        if(findStatistics == null) {
            long id = createStatistics(member,running,statisticsType);
            findStatistics = statisticsRepository.findById(id).get();
        }
        findStatistics.setDistance(findStatistics.getDistance()+running.getDistance());
        findStatistics.setCount(findStatistics.getCount()+1);
        findStatistics.setTotalSeconds(findStatistics.getTotalSeconds()+ running.getDuration());
        findStatistics.setEnergy(findStatistics.getEnergy()+running.getEnergy());


    }

    @Transactional
    public StatisticsDTO findDailyStatistics(Long memberID, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Calendar startTime = getCalendarStart(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Daily);
        Calendar endTime = getCalendarEnd(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Daily);

        Statistics statistics = statisticsRepository.findByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Daily, memberID,
                Timestamp.valueOf(dateFormat.format(startTime.getTime())), Timestamp.valueOf(dateFormat.format(endTime.getTime())));
        if(statistics == null) {
            return StatisticsDTO.builder().build();
        }else
            return StatisticsDTO.builder()
                    .distance(statistics.getDistance())
                    .totalSeconds(statistics.getTotalSeconds())
                    .pace((1000/statistics.getDistance())*(statistics.getTotalSeconds()/3600.0))
                    .energy(statistics.getEnergy())
                    .build();
    }

    @Transactional
    public StatisticsDTO findWeeklyStatistics(Long memberID, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        calendar.set(Calendar.DAY_OF_WEEK, 1);
        Calendar startTime = getCalendarStart(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Weekly);
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        Calendar endTime = getCalendarEnd(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Weekly);

        Statistics statistics = statisticsRepository.findByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Weekly, memberID,
                Timestamp.valueOf(dateFormat.format(startTime.getTime())), Timestamp.valueOf(dateFormat.format(endTime.getTime())));
        if(statistics == null) {
            return StatisticsDTO.builder().build();
        }else
            return StatisticsDTO.builder()
                .distance(statistics.getDistance())
                .totalSeconds(statistics.getTotalSeconds())
                .pace((1000/statistics.getDistance())*(statistics.getTotalSeconds()/3600.0))
                .energy(statistics.getEnergy())
                .build();
    }

    @Transactional
    public StatisticsDTO findMonthlyStatistics(Long memberID, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Calendar startTime = getCalendarStart(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Monthly);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Calendar endTime = getCalendarEnd(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Monthly);

        Statistics statistics = statisticsRepository.findByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Monthly, memberID,
                Timestamp.valueOf(dateFormat.format(startTime.getTime())), Timestamp.valueOf(dateFormat.format(endTime.getTime())));
        if(statistics == null) {
            return StatisticsDTO.builder().build();
        }else
            return StatisticsDTO.builder()
                .distance(statistics.getDistance())
                .totalSeconds(statistics.getTotalSeconds())
                .pace((1000/statistics.getDistance())*(statistics.getTotalSeconds()/3600.0))
                .energy(statistics.getEnergy())
                .build();
    }

    @Transactional
    public StatisticsDTO findYearlyStatistics(Long memberID, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        Calendar startTime = getCalendarStart(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Monthly);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.MONTH, 11);
        Calendar endTime = getCalendarEnd(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Monthly);

        List<Statistics> allStatistics = statisticsRepository.findAllByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Monthly, memberID,
                Timestamp.valueOf(dateFormat.format(startTime.getTime())), Timestamp.valueOf(dateFormat.format(endTime.getTime())));

        double distance = allStatistics.stream().mapToDouble(Statistics::getDistance).sum();
        double energy = allStatistics.stream().mapToDouble(Statistics::getEnergy).sum();
        double totalSeconds = allStatistics.stream().mapToDouble(Statistics::getTotalSeconds).sum();
        double pace = (1000/distance)*(totalSeconds/3600.0);

        return StatisticsDTO.builder()
                .distance(distance)
                .totalSeconds(totalSeconds)
                .pace((1000/distance)*(totalSeconds/3600.0))
                .energy(energy)
                .pace(pace)
                .build();

    }

    @Transactional
    public StatisticsDTO findTotalStatistics(Long memberID) {
        List<Statistics> allStatistics = statisticsRepository.findAllByStatisticsTypeAndMemberId(StatisticsType.Monthly, memberID);

        double distance = allStatistics.stream().mapToDouble(Statistics::getDistance).sum();
        double energy = allStatistics.stream().mapToDouble(Statistics::getEnergy).sum();
        double totalSeconds = allStatistics.stream().mapToDouble(Statistics::getTotalSeconds).sum();
        double pace = (1000/distance)*(totalSeconds/3600.0);

        return StatisticsDTO.builder()
                .distance(distance)
                .totalSeconds(totalSeconds)
                .pace(pace)
                .energy(energy)
                .build();
    }

    /**
     * @param memberID 특정 멤버의 아이디
     * @param calendar 특정 날짜
     * @return 특정 멤버의 월별 스트릭 정보
     */
    @Transactional
    public List<Double> findMonthlyStreak(Long memberID, Calendar calendar) {

        List<Double> monthlyStreak = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        //들어온 시간이 포함된 달의 첫날로 시간설정
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Calendar startTime = getCalendarStart(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Daily);
        Calendar endTime = getCalendarEnd(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Daily);

        for(int i =0; i<calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
            Statistics dailyStatistics = statisticsRepository.findByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Daily, memberID,
                    Timestamp.valueOf(dateFormat.format(startTime.getTime())),
                    Timestamp.valueOf(dateFormat.format(endTime.getTime())));

            if(dailyStatistics == null){monthlyStreak.add(0.0);}
            else{   monthlyStreak.add(dailyStatistics.getDistance());}

            startTime.roll(Calendar.DATE,1);
            endTime.roll(Calendar.DATE,1);
        }
        return monthlyStreak;
    }

    //내부 로직

    /**
     *
     * @param timestamp 설정하기 원하는 기준시간
     * @param statisticsType 설정하기 원하는 시간 타입(일/주/월)
     * @return 종료시간을 String 형식으로 반환
     */
    private Calendar getCalendarEnd(Timestamp timestamp, StatisticsType statisticsType) {

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(timestamp);
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
        return calendarEnd;
    }

    /**
     *
     * @param timestamp 설정하기 원하는 기준시간
     * @param statisticsType 설정하기 원하는 시간 타입(일/주/월)
     * @return 시작작간을 String형식으로 반환
     */
    private Calendar getCalendarStart(Timestamp timestamp, StatisticsType statisticsType) {

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(timestamp);
        if(statisticsType == StatisticsType.Weekly) calendarStart.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        else if(statisticsType == StatisticsType.Monthly) calendarStart.set(Calendar.DAY_OF_MONTH,1);

        calendarStart.set(Calendar.AM_PM,Calendar.AM);
        calendarStart.set(Calendar.HOUR,0);
        calendarStart.set(Calendar.MINUTE,0);
        calendarStart.set(Calendar.SECOND,0);
        calendarStart.set(Calendar.MILLISECOND,0);
        return calendarStart;
    }

    private Long createStatistics(Member member, Running running, StatisticsType statisticsType) {
        Statistics statistics = new Statistics();
        statistics.setMember(member);
        statistics.setTime(running.getStartTime());
        statistics.setStatisticsType(statisticsType);
        statisticsRepository.save(statistics);

        return statistics.getId();
    }


}
