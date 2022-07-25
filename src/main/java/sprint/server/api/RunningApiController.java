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

        Long runningId = runningService.addRun(request.getMemberId());
        return new CreateRunningResponse(runningId);

    }

    @PostMapping("/api/running/update")
    public UpdateRunningResponse updateRunning(@RequestBody @Valid UpdateRunningRequest request) {

        runningService.calculateRunningData(request.getRunningId(),request.getUserId(),request.getRunningData());

        Running running = runningService.findOne(request.getRunningId());
        return  new UpdateRunningResponse(running.getId(),running.getDistance(),running.getDuration(),running.getEnergy());
    }

    @Data
    static class CreateRunningResponse {

        private Long id;

        public CreateRunningResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateRunningRequest {
        private Long memberId;
    }

    @Data
    static class UpdateRunningResponse {
        private long runningId;
        private double distance;
        private double duration;
        private double energy;

        public UpdateRunningResponse(long runningId, double distance, double duration, double energy) {
            this.runningId = runningId;
            this.distance = distance;
            this.duration = duration;
            this.energy = energy;
        }
    }

    @Data
    static class UpdateRunningRequest {
        private Long userId;
        private Long runningId;
        private List<RunningRowData> runningData;
    }


}
