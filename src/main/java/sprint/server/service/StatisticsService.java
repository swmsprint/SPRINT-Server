package sprint.server.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.RunningRepository;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final MemberRepository memberRepository;
    private final RunningRepository runningRepository;

/*    @Transactional
    public Long*/

    /**
     * Daily, Weekly, Monthly 시간별로 파라미터를 만들어야함.
     */

    /**
     *  MemberId + (Daily, Weekly, Monthly) + (시간) 로 데이터롤 요청하는
     */
}
