package sprint.server.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.repository.MemberRepository;
import sprint.server.service.MemberService;
import sprint.server.service.StatisticsService;

@RestController
@RequiredArgsConstructor
public class StatisticsApiController {

    private final MemberService memberService;
    private final StatisticsService statisticsService;


}
