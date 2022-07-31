package sprint.server.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.domain.Running;
import sprint.server.domain.RunningRowData;
import sprint.server.repository.RunningRepository;
import sprint.server.service.RunningService;

import javax.swing.text.View;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RunningApiController {

    private final RunningService runningService;
    private final ObjectMapper objectMapper;

    @PostMapping("/api/running/start")
    public CreateRunningResponse createRunning(@RequestBody @Valid CreateRunningRequest request) {

        Long runningId = runningService.addRun(request.getUserId());
        return new CreateRunningResponse(runningId);

    }

    @PostMapping("/api/running/finish")
    public FinishRunningResponse finishRunning(@RequestBody @Valid RunningApiController.FinishRunningRequest request) throws JsonProcessingException {

        runningService.finishRunning(request.getRunningId(),request.getUserId(),request.getDuration(),request.getRunningData());
        Running running = runningService.findOne(request.getRunningId());

        return  new FinishRunningResponse(running.getId(),running.getDistance(),running.getDuration(),running.getEnergy());
    }

    @GetMapping("/api/running/{id}")
    public ViewRunningResponse viewRunningDetail(@PathVariable("id")Long runningId,
                                                 @RequestParam(value="memberId")Long memberId )throws JsonProcessingException{

        Running running = runningService.findOne(runningId);
        /**
         * 아직 러닝 정보 공개 정책이 없기때문에 전부 받아서 반환해줌 -> 추후 수정 필요
         */
        return new ViewRunningResponse(running.getId(),running.getDistance(),
                running.getDuration(),running.getEnergy(),
                Arrays.asList(objectMapper.readValue(running.getRowData(),RunningRowData[].class)));
    }


    /**
     * 러닝 생성후 반환할 응답
     */
    @Data
    static class CreateRunningResponse {

        private Long runningId;

        public CreateRunningResponse(Long runningId) {
            this.runningId = runningId;
        }
    }

    /**
     * 러닝 생성을 위해 받을 요청
     */
    @Data
    static class CreateRunningRequest {
        private Long userId;
    }

    /**
     * 러닝 종료 후 반환할 응답
     */
    @Data
    static class FinishRunningResponse {
        private long runningId;
        private double distance;
        private double duration;
        private double energy;

        public FinishRunningResponse(long runningId, double distance, double duration, double energy) {
            this.runningId = runningId;
            this.distance = distance;
            this.duration = duration;
            this.energy = energy;
        }
    }

    /**
     * 러닝 종료후 전달받은 요청 정보
     */
    @Data
    static class FinishRunningRequest {
        private long userId;
        private long runningId;
        private int duration;
        private List<RunningRowData> runningData;
    }
    @Data
    static class ViewRunningResponse {
        private long runningId;
        private double distance;
        private double duration;
        private double energy;
        private List<RunningRowData> runningData;

        public ViewRunningResponse(long runningId, double distance, double duration, double energy, List<RunningRowData> runningData) {
            this.runningId = runningId;
            this.distance = distance;
            this.duration = duration;
            this.energy = energy;
            this.runningData = runningData;
        }
    }
}
