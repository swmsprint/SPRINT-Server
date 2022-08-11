package sprint.server.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Member.Member;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.RunningRepository;
import sprint.server.repository.StatisticsRepository;

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
     * Daily, Weekly, Monthly 시간별로 파라미터를 만들어야함.
     */

    /**
     *  MemberId + (Daily, Weekly, Monthly) + (시간) 로 데이터롤 요청하는
     */
}
