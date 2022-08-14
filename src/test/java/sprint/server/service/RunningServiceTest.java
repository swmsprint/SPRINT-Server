package sprint.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.request.FinishRunningRequest;
import sprint.server.controller.datatransferobject.response.FinishRunningResponse;
import sprint.server.controller.datatransferobject.response.RunningInfoDTO;
import sprint.server.controller.datatransferobject.response.RunningRawDataVO;
import sprint.server.domain.Running;
import sprint.server.domain.member.Member;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.RunningRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class RunningServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired RunningRepository runningRepository;
    @Autowired RunningService runningService;


    @Test
    void addRun() {
        //Given
        Member member = memberRepository.findById(1L).orElse(null);
        //When
        long id = runningService.addRun(member,"2021-07-02 07:48:26.382");
        Running running = runningRepository.findByMember_IdAndAndStartTime(member.getId(), Timestamp.valueOf("2021-07-02 07:48:26.382"));

        //Then
        assertEquals(running.getId(), id);
    }

    @Test
    void finishRunning() throws JsonProcessingException {
        //Given
        Member member = memberRepository.findById(1L).orElse(null);
        long id = runningService.addRun(member,"2021-07-02 07:48:26.382");
        Running running = runningRepository.findByMember_IdAndAndStartTime(member.getId(), Timestamp.valueOf("2021-07-02 07:48:26.382"));
        List<RunningRawDataVO>  runningRawData= new ArrayList<>();
        runningRawData.add(new RunningRawDataVO(37.33028771,-122.02810514, 4.05,"2022-08-02 07:48:26.382Z"));
        runningRawData.add(new RunningRawDataVO(37.33028312,-122.02805328, 4.05,"2022-08-02 07:48:27.310Z"));
        runningRawData.add(new RunningRawDataVO(37.33028179,-122.02799851, 4.21,"2022-08-02 07:48:28.280Z"));
        runningRawData.add(new RunningRawDataVO(37.33027655,-122.02794361, 4.2,"2022-08-02 07:48:29.391Z"));

        FinishRunningRequest tempRequest = new FinishRunningRequest(member.getId(), id, 3, runningRawData);

        //When
        runningService.finishRunning(tempRequest);

        //Then
        assertEquals(running.getDuration(),3);
    }


    @Test
    void fetchRunningPagesBy() throws JsonProcessingException {
        //Given
        List<RunningRawDataVO>  runningRawData= new ArrayList<>();
        runningRawData.add(new RunningRawDataVO(37.33028771,-122.02810514, 4.05,"2022-08-02 07:48:26.382Z"));
        runningRawData.add(new RunningRawDataVO(37.33028312,-122.02805328, 4.05,"2022-08-02 07:48:27.310Z"));
        runningRawData.add(new RunningRawDataVO(37.33028179,-122.02799851, 4.21,"2022-08-02 07:48:28.280Z"));
        runningRawData.add(new RunningRawDataVO(37.33027655,-122.02794361, 4.2,"2022-08-02 07:48:29.391Z"));

        Member member = memberRepository.findById(1L).orElse(null);
        long running1Id = runningService.addRun(member,"2021-07-02 07:48:26.382");
        FinishRunningRequest tempRequest1 = new FinishRunningRequest(member.getId(), running1Id, 3, runningRawData);
        runningService.finishRunning(tempRequest1);

        long running2Id = runningService.addRun(member,"2021-08-03 07:48:26.382");
        FinishRunningRequest tempRequest2 = new FinishRunningRequest(member.getId(), running2Id, 3, runningRawData);
        runningService.finishRunning(tempRequest2);

        long running3Id = runningService.addRun(member,"2022-08-01 07:48:26.382");
        FinishRunningRequest tempRequest3 = new FinishRunningRequest(member.getId(), running3Id, 3, runningRawData);
        runningService.finishRunning(tempRequest3);

        long running4Id = runningService.addRun(member,"2022-08-13 07:48:26.382");
        FinishRunningRequest tempRequest4 = new FinishRunningRequest(member.getId(), running4Id, 3, runningRawData);
        runningService.finishRunning(tempRequest4);

        //When
        Page<Running>  runnings = runningService.fetchRunningPagesBy(running4Id,member.getId());
        //Then

        assertEquals(runnings.getContent().get(0).getId(),running3Id);
        assertEquals(runnings.getContent().get(1).getId(),running2Id);
        assertEquals(runnings.getContent().get(2).getId(),running1Id);
    }

}