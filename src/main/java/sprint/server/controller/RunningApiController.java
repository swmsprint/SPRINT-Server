package sprint.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.controller.datatransferobject.request.CreateRunningRequest;
import sprint.server.controller.datatransferobject.request.FinishRunningRequest;
import sprint.server.domain.Running;
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

    @ApiOperation(value="러닝 시작", notes = "성공시 저장된 runningId를 반환합니다")
    @PostMapping("/api/running/start")
    public CreateRunningResponse createRunning(@RequestBody @Valid CreateRunningRequest request) {
        Member member = memberService.findById(request.getUserId());
        Long runningId = runningService.addRun(member,request.getStartTime());
        return new CreateRunningResponse(runningId);
    }

    @ApiOperation(value="러닝 종료", notes = "성공시 저장및 계산된 running 정보를 반환합니다")
    @PostMapping("/api/running/finish")
    public FinishRunningResponse finishRunning(@RequestBody @Valid FinishRunningRequest request) throws JsonProcessingException {
        Running running = runningService.finishRunning(request);
        statisticsService.updateStatistics(running, StatisticsType.Daily);
        statisticsService.updateStatistics(running, StatisticsType.Weekly);
        statisticsService.updateStatistics(running, StatisticsType.Monthly);

        return new FinishRunningResponse(running.getId(),running.getDistance(),running.getDuration(),running.getEnergy());
    }


    @ApiOperation(value="러닝 정보 반환", notes = "성공시 저장된 running 정보의 자세한 정보들을 반환합니다")
    @GetMapping("/api/running/detail")
    public ViewRunningResponse viewRunningDetail(@RequestParam(value="runningId")Long runningId,
                                                 @RequestParam(value="userId")Long memberId )throws JsonProcessingException{
        Running running = runningService.findOne(runningId).get();
        /**
         * 아직 러닝 정보 공개 정책이 없기때문에 전부 받아서 반환해줌 -> 추후 수정 필요
         */

        return new ViewRunningResponse(running.getId(),running.getDistance(),
                running.getDuration(),running.getEnergy(),
                Arrays.asList(objectMapper.readValue(running.getRawData(), RunningRawDataVO[].class)));
    }


    @ApiOperation(value="최근 러닝 정보 반환", notes = "성공시 저장된 3개의 running 정보를 반환합니다")
    @GetMapping("/api/runnings")
    public List<RunningInfoDTO> viewRecentRunning(@RequestParam(value="userId")Long memberId,
                                                  @RequestParam(value="lastRunningId")Long lastRunningId){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return runningService.fetchRunningPagesBy(lastRunningId,memberId).toList().stream()
                .map(running -> new RunningInfoDTO(running.getId(),running.getDuration(),running.getDistance(),dateFormat.format(running.getStartTime()),running.getEnergy()))
                .collect(java.util.stream.Collectors.toList());
    }


}
