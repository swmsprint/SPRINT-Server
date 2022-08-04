package sprint.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.CreateRunningRequest;
import sprint.server.controller.datatransferobject.request.FinishRunningRequest;
import sprint.server.controller.datatransferobject.response.CreateRunningResponse;
import sprint.server.controller.datatransferobject.response.FinishRunningResponse;
import sprint.server.controller.datatransferobject.response.ViewRunningResponse;
import sprint.server.domain.Member;
import sprint.server.domain.Running;
import sprint.server.domain.RunningRowData;
import sprint.server.service.MemberService;
import sprint.server.service.RunningService;

import javax.validation.Valid;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class RunningApiController {

    private final RunningService runningService;
    private final ObjectMapper objectMapper;
    private final MemberService memberService;

    @PostMapping("/api/running/start")
    public CreateRunningResponse createRunning(@RequestBody @Valid CreateRunningRequest request) {

        Member member = memberService.findById(request.getUserId());
        Long runningId = runningService.addRun(member);
        return new CreateRunningResponse(runningId);

    }

    @PostMapping("/api/running/finish")
    public FinishRunningResponse finishRunning(@RequestBody @Valid FinishRunningRequest request) throws JsonProcessingException {

        runningService.finishRunning(request.getRunningId(),request.getUserId(),request.getDuration(),request.getRunningData());
        Running running = runningService.findOne(request.getRunningId()).get();

        return  new FinishRunningResponse(running.getId(),running.getDistance(),running.getDuration(),running.getEnergy());
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
                Arrays.asList(objectMapper.readValue(running.getRowData(),RunningRowData[].class)));
    }


}
