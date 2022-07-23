package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Member;
import sprint.server.domain.Running;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.RunningRepository;

@Service
@RequiredArgsConstructor
public class RunningService {

    private final MemberRepository memberRepository;
    private final RunningRepository runningRepository;

    @Transactional
    public Long addRun(Long memberId){
        Member member = memberRepository.findOne(memberId);

        Running running = Running.createRunning(member);
        runningRepository.save(running);

        return running.getId();

    }

}
