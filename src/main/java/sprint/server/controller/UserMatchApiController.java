package sprint.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.response.ApplyUserMatchResponse;
import sprint.server.domain.usermatch.UserMatchApply;
import sprint.server.service.MemberService;
import sprint.server.service.UserMatchService;

import java.util.Calendar;

@RestController
@RequiredArgsConstructor
@RequestMapping("/match/user/")
public class UserMatchApiController {

    private final UserMatchService userMatchService;
    private final MemberService memberService;
    @GetMapping("apply/{id}")
    public ApplyUserMatchResponse applyUserMatch(@PathVariable("id")Long memberID){
        memberService.findById(memberID);
        Calendar calendar = Calendar.getInstance();
        UserMatchApply applyResult = userMatchService.saveUserApplyMatchInfo(memberID,calendar);
        return new ApplyUserMatchResponse(applyResult.getMatchId());
    }


}
