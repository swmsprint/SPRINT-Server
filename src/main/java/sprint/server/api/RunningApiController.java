package sprint.server.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.domain.Running;
import sprint.server.domain.RunningRowData;
import sprint.server.service.RunningService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RunningApiController {

    private final RunningService runningService;

    @PostMapping("/api/running/start")
    public CreateRunningResponse createRunning(@RequestBody @Valid CreateRunningRequest request) {

        Long runningId = runningService.addRun(request.getUserId());
        return new CreateRunningResponse(runningId);

    }

    @PostMapping("/api/running/finish")
    public FinishRunningResponse finishRunning(@RequestBody @Valid FinishRunningRequest request) {

        runningService.finishRunning(request.getRunningId(),request.getUserId(),request.getDuration(),request.getRunningData());
        Running running = runningService.findOne(request.getRunningId()).get();

        return  new FinishRunningResponse(running.getId(),running.getDistance(),running.getDuration(),running.getEnergy());
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
        private Long userId;
        private Long runningId;
        private int duration;
        private List<RunningRowData> runningData;
    }


}
