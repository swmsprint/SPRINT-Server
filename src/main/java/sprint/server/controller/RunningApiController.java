package sprint.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.response.RunningInfoDTO;
import sprint.server.controller.datatransferobject.request.CreateRunningRequest;
import sprint.server.controller.datatransferobject.request.FinishRunningRequest;
import sprint.server.controller.datatransferobject.response.CreateRunningResponse;
import sprint.server.controller.datatransferobject.response.FinishRunningResponse;
import sprint.server.controller.datatransferobject.response.ViewRunningResponse;
import sprint.server.domain.Running;
import sprint.server.controller.datatransferobject.response.RunningRawDataVo;
import sprint.server.domain.member.Member;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.service.MemberService;
import sprint.server.service.RunningService;
import sprint.server.service.StatisticsService;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RunningApiController {

    private final RunningService runningService;
    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final StatisticsService statisticsService;

    @PostMapping("/api/running/start")
    public CreateRunningResponse createRunning(@RequestBody @Valid CreateRunningRequest request) {
        Member member = memberService.findById(request.getUserId());
        Long runningId = runningService.addRun(member,request.getStartTime());
        return new CreateRunningResponse(runningId);
    }

    @PostMapping("/api/running/finish")
    public FinishRunningResponse finishRunning(@RequestBody @Valid FinishRunningRequest request) throws JsonProcessingException {
        runningService.finishRunning(request);
        Running running = runningService.findOne(request.getRunningId()).get();
        statisticsService.updateStatistics(running, StatisticsType.Daily);
        statisticsService.updateStatistics(running, StatisticsType.Weekly);
        statisticsService.updateStatistics(running, StatisticsType.Monthly);

        return new FinishRunningResponse(running.getId(),running.getDistance(),running.getDuration(),running.getEnergy());
    }

    @GetMapping("/api/running/{id}")
    public ViewRunningResponse viewRunningDetail(@PathVariable("id")Long runningId,
                                                 @RequestParam(value="memberId")Long memberId )throws JsonProcessingException{
        Running running = runningService.findOne(runningId).get();
        /**
         * 아직 러닝 정보 공개 정책이 없기때문에 전부 받아서 반환해줌 -> 추후 수정 필요
         */
        return new ViewRunningResponse(running.getId(),running.getDistance(),
                running.getDuration(),running.getEnergy(),
                Arrays.asList(objectMapper.readValue(running.getRawData(), RunningRawDataVo[].class)));
    }

    @GetMapping("/api/runnings")
    public List<RunningInfoDTO> viewRecentRunning(@RequestParam(value="memberId")Long memberId,
                                                  @RequestParam(value="lastRunningId")Long lastRunningId){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return runningService.fetchRunningPagesBy(lastRunningId,memberId).toList().stream()
                .map(running -> new RunningInfoDTO(running.getId(),running.getDuration(),running.getDistance(),dateFormat.format(running.getStartTime()),running.getEnergy()))
                .collect(java.util.stream.Collectors.toList());
    }


}
