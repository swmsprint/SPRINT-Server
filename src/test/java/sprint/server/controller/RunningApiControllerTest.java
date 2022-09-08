package sprint.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.request.CreateRunningRequest;
import sprint.server.controller.datatransferobject.request.FinishRunningRequest;
import sprint.server.controller.datatransferobject.response.PersonalRunningInfoDTO;
import sprint.server.domain.RunningRawData;
import sprint.server.domain.Running;
import sprint.server.domain.member.Member;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.RunningRepository;
import sprint.server.service.RunningService;
import sprint.server.service.StatisticsService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RunningApiControllerTest {
    @Autowired private RunningService runningService;
    @Autowired private RunningRepository runningRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private StatisticsService statisticsService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("러닝 시작 정보 저장(성공)")
    void createRunningApi_Test() throws Exception {

//        //given
//        String url = "/api/running/start";
//        CreateRunningRequest request = new CreateRunningRequest(1L,"2021-07-02 07:48:26.382");
//
//        //when
//        ResultActions resultActions = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));
//        Running running = runningRepository.findByMember_IdAndAndStartTime(1L, Timestamp.valueOf("2021-07-02 07:48:26.382"));
//        //then
//        resultActions.andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(content().string("{\"runningId\":"+running.getId()+"}"));
    }

    @Test
    @DisplayName("러닝 종료 후 정보 저장(성공)")
    void finishRunningApi_Test() throws Exception {
//        //given
//        List<RunningRawData>  runningRawData= new ArrayList<>();
//        runningRawData.add(new RunningRawData(37.33028771,-122.02810514, 4.05,"2022-08-02 07:48:26.382Z"));
//        runningRawData.add(new RunningRawData(37.33028312,-122.02805328, 4.05,"2022-08-02 07:48:27.310Z"));
//        runningRawData.add(new RunningRawData(37.33028179,-122.02799851, 4.21,"2022-08-02 07:48:28.280Z"));
//        runningRawData.add(new RunningRawData(37.33027655,-122.02794361, 4.2,"2022-08-02 07:48:29.391Z"));
//
//        Member member = memberRepository.findById(1L).orElse(null);
//        long runningId = runningService.addRun(member,"2021-07-02 07:48:26.382");
//        FinishRunningRequest request = new FinishRunningRequest(member.getId(), runningId, 3, runningRawData);
//        String url = "/api/running/finish";
//
//        //when
//        ResultActions resultActions = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));
//        Running running = runningRepository.findByMember_IdAndAndStartTime(1L, Timestamp.valueOf("2021-07-02 07:48:26.382"));
//
//        //then
//        resultActions.andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(content().string("{\"runningId\":"+running.getId()+",\"distance\":"+running.getDistance()+",\"duration\":"+running.getDuration()+",\"energy\":"+running.getEnergy()+"}"));

    }

    @Test
    @DisplayName("러닝 상세정보(러닝 로우데이터) 반환(성공)")
    void viewRunningDetailApi_Test() throws Exception {
        //given
//        List<RunningRawData>  runningRawData= new ArrayList<>();
//        runningRawData.add(new RunningRawData(37.33028771,-122.02810514, 4.05,"2022-08-02 07:48:26.382Z"));
//        runningRawData.add(new RunningRawData(37.33028312,-122.02805328, 4.05,"2022-08-02 07:48:27.310Z"));
//        runningRawData.add(new RunningRawData(37.33028179,-122.02799851, 4.21,"2022-08-02 07:48:28.280Z"));
//        runningRawData.add(new RunningRawData(37.33027655,-122.02794361, 4.2,"2022-08-02 07:48:29.391Z"));
//
//        Member member = memberRepository.findById(1L).orElse(null);
//        long runningId = runningService.addRun(member,"2021-07-02 07:48:26.382");
//        FinishRunningRequest request = new FinishRunningRequest(member.getId(), runningId, 3, runningRawData);
//        Running running = runningService.finishRunning(request);
//        String url = "/api/running/detail"+"?runningId="+running.getId()+"&memberId="+member.getId();
//
//        //when
//        mvc.perform(get(url))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().string(objectMapper.writeValueAsString(request.getRunningData())));

    }

    @Test
    @DisplayName("최근 3개 러닝 데이터 List로 반환(성공)")
    void viewRecentRunningApi_Test() throws Exception {
        //Given
//        List<RunningRawData>  runningRawData= new ArrayList<>();
//        runningRawData.add(new RunningRawData(37.33028771,-122.02810514, 4.05,"2022-08-02 07:48:26.382Z"));
//        runningRawData.add(new RunningRawData(37.33028312,-122.02805328, 4.05,"2022-08-02 07:48:27.310Z"));
//        runningRawData.add(new RunningRawData(37.33028179,-122.02799851, 4.21,"2022-08-02 07:48:28.280Z"));
//        runningRawData.add(new RunningRawData(37.33027655,-122.02794361, 4.2,"2022-08-02 07:48:29.391Z"));
//
//        Member member = memberRepository.findById(1L).orElse(null);
//        long running1Id = runningService.addRun(member,"2021-07-02 07:48:26.382");
//        FinishRunningRequest tempRequest1 = new FinishRunningRequest(member.getId(), running1Id, 3, runningRawData);
//        Running running1 = runningService.finishRunning(tempRequest1);
//
//        long running2Id = runningService.addRun(member,"2021-08-03 07:48:26.382");
//        FinishRunningRequest tempRequest2 = new FinishRunningRequest(member.getId(), running2Id, 3, runningRawData);
//        Running running2 = runningService.finishRunning(tempRequest2);
//
//        long running3Id = runningService.addRun(member,"2022-08-01 07:48:26.382");
//        FinishRunningRequest tempRequest3 = new FinishRunningRequest(member.getId(), running3Id, 3, runningRawData);
//        Running running3 = runningService.finishRunning(tempRequest3);
//
//        long running4Id = runningService.addRun(member,"2022-08-13 07:48:26.382");
//        FinishRunningRequest tempRequest4 = new FinishRunningRequest(member.getId(), running4Id, 3, runningRawData);
//        Running running4 =runningService.finishRunning(tempRequest4);
//
//        //when
//        String url = "/api/runnings"+"?memberId="+member.getId()+"&lastRunningId="+running4Id;
//        List<PersonalRunningInfoDTO> expected = new ArrayList<>();
//        expected.add(new PersonalRunningInfoDTO(running3Id, 3, running3.getDistance(), "2022-08-01 07:48:26.382",running3.getEnergy()));
//        expected.add(new PersonalRunningInfoDTO(running2Id, 3, running2.getDistance(), "2021-08-03 07:48:26.382",running2.getEnergy()));
//        expected.add(new PersonalRunningInfoDTO(running1Id, 3, running1.getDistance(), "2021-07-02 07:48:26.382",running1.getEnergy()));
//
//        //then
//        mvc.perform(get(url))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().string(objectMapper.writeValueAsString(expected)));
    }
}