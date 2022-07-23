package sprint.server.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.service.RunningService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class RunningApiController {

    private final RunningService runningService;

    @PostMapping("/api/running")
    public CreateRunningResponse saveRunning(@RequestBody @Valid CreateRunningRequest request){

        System.out.println(request.getMemberId());
        Long runningId = runningService.addRun(request.getMemberId());
        return new CreateRunningResponse(runningId);

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

}
