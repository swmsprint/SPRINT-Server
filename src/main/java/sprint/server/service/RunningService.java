package sprint.server.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Member;
import sprint.server.domain.Running;
import sprint.server.domain.RunningRowData;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.RunningRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunningService {

    private final MemberRepository memberRepository;
    private final RunningRepository runningRepository;


    public Running findOne(Long runningId){
        return runningRepository.findOne(runningId);
    }
    @Transactional
    public Long addRun(Long memberId){
        Member member = memberRepository.findOne(memberId);

        Running running = Running.createRunning(member);
        runningRepository.save(running);

        return running.getId();

    }

    @Transactional
    public void calculateRunningData(Long runningId, Long memberId, List<RunningRowData> rowData){

        Running running = runningRepository.findOne(runningId);
        Member member = memberRepository.findOne(memberId);

        /**
         * 계산 과정 추가할 것!
         */

        //예시 데이터
        int duration = 2332;
        double distance = 1222.3;
        double energy = 122.3;
        float weight = member.getWeight();

        running.setDuration(duration);
        running.setDistance(distance);
        running.setRowData(rowData.toString());
    }

}
