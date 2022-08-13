package sprint.server.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sprint.server.domain.Running;
import sprint.server.domain.member.Member;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.RunningRepository;
import sprint.server.service.StatisticsService;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
@AutoConfigureMockMvc
class StatisticsApiControllerTest {

    @Autowired private MemberRepository memberRepository;

    @Autowired private RunningRepository runningRepository;
    @Autowired private StatisticsService statisticsService;

    @Autowired private MockMvc mvc;

    @Test
    void viewStatisticsDetail_Test() throws Exception {
        //given
        Member member =  memberRepository.findById(1L).orElse(null);

        Running running1 = new Running();
        running1.setStartTime(Timestamp.valueOf("2022-08-01 07:48:29.391"));
        running1.setDistance(12.323);
        running1.setEnergy(213);
        running1.setWeight(80);
        running1.setDuration(12);
        running1.setMember(member);
        runningRepository.save(running1);
        statisticsService.updateStatistics(running1, StatisticsType.Daily);
        statisticsService.updateStatistics(running1, StatisticsType.Weekly);
        statisticsService.updateStatistics(running1, StatisticsType.Monthly);

        Running running2 = new Running();
        running2.setStartTime(Timestamp.valueOf("2022-08-12 07:48:29.391"));
        running2.setDistance(15.23);
        running2.setEnergy(522);
        running2.setWeight(81);
        running2.setDuration(9);
        running2.setMember(member);
        runningRepository.save(running2);
        statisticsService.updateStatistics(running2, StatisticsType.Daily);
        statisticsService.updateStatistics(running2, StatisticsType.Weekly);
        statisticsService.updateStatistics(running2, StatisticsType.Monthly);

        Running running3 = new Running();
        running3.setStartTime(Timestamp.valueOf("2021-08-01 07:48:29.391"));
        running3.setDistance(51.2);
        running3.setEnergy(323);
        running3.setWeight(74);
        running3.setDuration(10);
        running3.setMember(member);
        runningRepository.save(running3);
        statisticsService.updateStatistics(running3, StatisticsType.Daily);
        statisticsService.updateStatistics(running3, StatisticsType.Weekly);
        statisticsService.updateStatistics(running3, StatisticsType.Monthly);

        //when
        String url = "/api/statistics/"+member.getId();

        //then
        mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void viewStreakDetail() throws Exception{
        //given
        Member member = memberRepository.findById(2L).orElse(null);

        Running running1 = new Running();
        running1.setStartTime(Timestamp.valueOf("2022-08-01 07:48:29.391"));
        running1.setDistance(12.323);
        running1.setEnergy(213);
        running1.setWeight(80);
        running1.setDuration(12);
        running1.setMember(member);
        runningRepository.save(running1);
        statisticsService.updateStatistics(running1, StatisticsType.Daily);
        statisticsService.updateStatistics(running1, StatisticsType.Weekly);
        statisticsService.updateStatistics(running1, StatisticsType.Monthly);

        Running running2 = new Running();
        running2.setStartTime(Timestamp.valueOf("2022-08-12 07:48:29.391"));
        running2.setDistance(15.23);
        running2.setEnergy(522);
        running2.setWeight(81);
        running2.setDuration(9);
        running2.setMember(member);
        runningRepository.save(running2);
        statisticsService.updateStatistics(running2, StatisticsType.Daily);
        statisticsService.updateStatistics(running2, StatisticsType.Weekly);
        statisticsService.updateStatistics(running2, StatisticsType.Monthly);

        List<Double> result = new ArrayList<>(Arrays.asList(new Double[]{12.323,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,15.23,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0}));
        //when
        String url = "/api/statistics/streak/"+member.getId()+"?year="+2022+"&month="+8;

        //then
        mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(result));
    }
}