package sprint.server.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.response.StatisticsInfoVO;
import sprint.server.domain.Running;
import sprint.server.domain.member.Member;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.StatisticsRepository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final MemberRepository memberRepository;
    private final StatisticsRepository statisticsRepository;

    @Transactional
    public Long createStatistics(Member member,Timestamp timestamp, StatisticsType statisticsType) {
        Statistics statistics = Statistics.builder().build();
        statistics.setMember(member);
        statistics.setTime(timestamp);
        statistics.setStatisticsType(statisticsType);
        statisticsRepository.save(statistics);

        return statistics.getId();
    }

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
        Timestamp timeSection = Timestamp.valueOf(dateFormat.format(
                getCalendarStart(new Timestamp(Calendar.getInstance().getTimeInMillis()),statisticsType).getTime()));

        //만약 아직 업데이트 이전인 이번 주/월/년의 데이터가 들어왔다면 업데이트하지 않고 내버려둠
        //이번 섹션지나고 나중에 한번에 업데이트 예정
        if(statisticsType != StatisticsType.Daily && timeSection.getTime()<timeEnd.getTime()) return;

        //그 이후 생성된 statistics를 찾는다
        Statistics findStatistics = statisticsRepository.findByStatisticsTypeAndMemberIdAndTimeBetween(
                statisticsType, memberRepository.findById(member.getId()).get().getId(), timeStart, timeEnd);


        //만약 존재하지 않으면
        if(findStatistics == null) {
            long id = createStatistics(member,running.getStartTime(),statisticsType);
            findStatistics = statisticsRepository.findById(id).get();
        }

        findStatistics.setDistance(findStatistics.getDistance()+running.getDistance());
        findStatistics.setCount(findStatistics.getCount()+1);
        findStatistics.setTotalSeconds(findStatistics.getTotalSeconds()+ running.getDuration());
        findStatistics.setEnergy(findStatistics.getEnergy()+running.getEnergy());


    }

    @Transactional
    public StatisticsInfoVO findDailyStatistics(Long memberID, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


        Calendar startTime = getCalendarStart(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Daily);
        Calendar endTime = getCalendarEnd(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Daily);

        Statistics statistics = statisticsRepository.findByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Daily, memberID,
                Timestamp.valueOf(dateFormat.format(startTime.getTime())), Timestamp.valueOf(dateFormat.format(endTime.getTime())));
        if(statistics == null) {
            return StatisticsInfoVO.builder().build();
        }else
            return StatisticsInfoVO.builder()
                    .distance(statistics.getDistance())
                    .totalSeconds(statistics.getTotalSeconds())
                    .energy(statistics.getEnergy())
                    .build();
    }

    @Transactional
    public StatisticsInfoVO findWeeklyStatistics(Long memberID, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


        Calendar startTime = getCalendarStart(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Weekly);
        Calendar endTime = getCalendarEnd(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Weekly);

        List<Statistics> dailyStatistics = statisticsRepository.findAllByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Daily, memberID,
                Timestamp.valueOf(dateFormat.format(startTime.getTime())), Timestamp.valueOf(dateFormat.format(endTime.getTime())));

        double distance =dailyStatistics.stream().mapToDouble(Statistics::getDistance).sum();
        double energy =dailyStatistics.stream().mapToDouble(Statistics::getEnergy).sum();
        double totalSeconds = dailyStatistics.stream().mapToDouble(Statistics::getTotalSeconds).sum();

        return StatisticsInfoVO.builder()
                .distance(distance)
                .totalSeconds(totalSeconds)
                .energy(energy)
                .build();

    }

    @Transactional
    public StatisticsInfoVO findMonthlyStatistics(Long memberID, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StatisticsInfoVO lastWeekStatistics = findWeeklyStatistics(memberID,calendar);

        Calendar startTime = getCalendarStart(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Monthly);
        Calendar endTime = getCalendarEnd(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Monthly);

        List<Statistics> weeklyStatistics = statisticsRepository.findAllByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Weekly, memberID,
                Timestamp.valueOf(dateFormat.format(startTime.getTime())), Timestamp.valueOf(dateFormat.format(endTime.getTime())));


        double distance = lastWeekStatistics.getDistance()
                + weeklyStatistics.stream().mapToDouble(Statistics::getDistance).sum();
        double energy = lastWeekStatistics.getEnergy()
                + weeklyStatistics.stream().mapToDouble(Statistics::getEnergy).sum();
        double totalSeconds = lastWeekStatistics.getTotalSeconds()
                + weeklyStatistics.stream().mapToDouble(Statistics::getTotalSeconds).sum();

        return StatisticsInfoVO.builder()
                .distance(distance)
                .totalSeconds(totalSeconds)
                .energy(energy)
                .build();
    }

    @Transactional
    public StatisticsInfoVO findYearlyStatistics(Long memberID, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StatisticsInfoVO lastMonthStatistics = findMonthlyStatistics(memberID,calendar);

        Calendar startTime = getCalendarStart(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Yearly);
        Calendar endTime = getCalendarEnd(Timestamp.valueOf(dateFormat.format(calendar.getTime())),StatisticsType.Yearly);

        List<Statistics> allStatistics = statisticsRepository.findAllByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Monthly, memberID,
                Timestamp.valueOf(dateFormat.format(startTime.getTime())), Timestamp.valueOf(dateFormat.format(endTime.getTime())));

        double distance = allStatistics.stream().mapToDouble(Statistics::getDistance).sum()
                +lastMonthStatistics.getDistance();
        double energy = allStatistics.stream().mapToDouble(Statistics::getEnergy).sum()
                +lastMonthStatistics.getEnergy();
        double totalSeconds = allStatistics.stream().mapToDouble(Statistics::getTotalSeconds).sum()
                +lastMonthStatistics.getTotalSeconds();

        return StatisticsInfoVO.builder()
                .distance(distance)
                .totalSeconds(totalSeconds)
                .energy(energy)
                .build();

    }

    @Transactional
    public StatisticsInfoVO findTotalStatistics(Long memberID,Calendar calendar) {

        StatisticsInfoVO lastYearlyStatistics = findYearlyStatistics(memberID,calendar);

        List<Statistics> allStatistics = statisticsRepository.findByStatisticsTypeAndMemberId(StatisticsType.Yearly, memberID);

        double distance = allStatistics.stream().mapToDouble(Statistics::getDistance).sum()
                +lastYearlyStatistics.getDistance();
        double energy = allStatistics.stream().mapToDouble(Statistics::getEnergy).sum()
                +lastYearlyStatistics.getEnergy();
        double totalSeconds = allStatistics.stream().mapToDouble(Statistics::getTotalSeconds).sum()
                +lastYearlyStatistics.getTotalSeconds();

        return StatisticsInfoVO.builder()
                .distance(distance)
                .totalSeconds(totalSeconds)
                .energy(energy)
                .build();

    }



    public StatisticsInfoVO findStatistics(Long memberID, Calendar calendar, StatisticsType statisticsType) {
        switch (statisticsType){
            case Daily:
                return findDailyStatistics(memberID,calendar);
            case Weekly:
                return findWeeklyStatistics(memberID,calendar);
            case Monthly:
                return findMonthlyStatistics(memberID,calendar);
            case Yearly:
                return findYearlyStatistics(memberID,calendar);
            case Totally:
                return findTotalStatistics(memberID,calendar);
        }
        return StatisticsInfoVO.builder().build();
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

            if(dailyStatistics == null)monthlyStreak.add(0.0);
            else monthlyStreak.add(dailyStatistics.getDistance());

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
            if(calendarEnd.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
                calendarEnd.add(Calendar.DATE,-1);
            }
            calendarEnd.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            calendarEnd.add(Calendar.DATE, 7);
        }
        else if(statisticsType == StatisticsType.Monthly){
            calendarEnd.set(Calendar.DAY_OF_MONTH,calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        }else if(statisticsType == StatisticsType.Yearly){
            calendarEnd.set(Calendar.DAY_OF_YEAR,calendarEnd.getActualMaximum(Calendar.DAY_OF_YEAR));
        }


        calendarEnd.set(Calendar.AM_PM,Calendar.PM);
        calendarEnd.set(Calendar.HOUR,11);
        calendarEnd.set(Calendar.MINUTE,59);
        calendarEnd.set(Calendar.SECOND,59);
        calendarEnd.set(Calendar.MILLISECOND,999);

//        log.info(statisticsType+"statistics log - "+"calendarEnd:"+calendarEnd.getTime());

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
        if(statisticsType == StatisticsType.Weekly) {
            if(calendarStart.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
                calendarStart.add(Calendar.DATE,-1);
            }
            calendarStart.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        }
        else if(statisticsType == StatisticsType.Monthly) calendarStart.set(Calendar.DAY_OF_MONTH,1);
        else if(statisticsType == statisticsType.Yearly) calendarStart.set(Calendar.DAY_OF_YEAR,1);
        calendarStart.set(Calendar.AM_PM,Calendar.AM);
        calendarStart.set(Calendar.HOUR,0);
        calendarStart.set(Calendar.MINUTE,0);
        calendarStart.set(Calendar.SECOND,0);
        calendarStart.set(Calendar.MILLISECOND,0);

//        log.info(statisticsType+"statistics log - "+"calendarStart:"+calendarStart.getTime());

        return calendarStart;
    }


    /**주기적으로 통계 저장**/
    public void savePreviousStatistics(StatisticsType statisticsType) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        List<Member> members = memberRepository.findAll();

        //시작 시간 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Timestamp timeStart = Timestamp.valueOf(dateFormat.format(
                getCalendarStart(new Timestamp(calendar.getTimeInMillis()), statisticsType).getTime()));

        Timestamp timeEnd = Timestamp.valueOf(dateFormat.format(
                getCalendarEnd(new Timestamp(calendar.getTimeInMillis()), statisticsType).getTime()));

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(timeStart.getTime());
        Calendar end = Calendar.getInstance();
        start.setTimeInMillis(timeEnd.getTime());
        //전체 멤버에 대해서
        for (Member member : members) {

            StatisticsInfoVO statisticsInfoVO = findStatistics(member.getId(), calendar,statisticsType);
            Statistics findStatistic = statisticsRepository.findByStatisticsTypeAndMemberIdAndTimeBetween(
                    StatisticsType.Weekly, memberRepository.findById(member.getId()).get().getId(), timeStart, timeEnd);
            log.info("calendarStart:"+start.getTime()+" calendarEnd"+end.getTime()+" memberId"+member.getId());
            //만약 기존 통계가 있으면 그걸 업데이트
            if (findStatistic != null) {
                findStatistic.setDistance(statisticsInfoVO.getDistance());
                findStatistic.setTotalSeconds(statisticsInfoVO.getTotalSeconds());
                findStatistic.setEnergy(statisticsInfoVO.getEnergy());
                findStatistic.setTime(new Timestamp(calendar.getTimeInMillis()));
                continue;
            }
            //없으면 새로 만듬
            Statistics statistics = Statistics.builder()
                    .member(member)
                    .distance(statisticsInfoVO.getDistance())
                    .totalSeconds(statisticsInfoVO.getTotalSeconds())
                    .energy(statisticsInfoVO.getEnergy())
                    .time(new Timestamp(calendar.getTimeInMillis()))
                    .statisticsType(statisticsType)
                    .build();
            statisticsRepository.save(statistics);
        }

    }


}
