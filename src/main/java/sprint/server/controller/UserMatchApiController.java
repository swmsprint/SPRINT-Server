package sprint.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.response.ApplyUserMatchResponse;
import sprint.server.controller.datatransferobject.response.LeagueInfoResponse;
import sprint.server.domain.member.Member;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.domain.usermatch.UserMatchApply;
import sprint.server.service.MemberService;
import sprint.server.service.StatisticsService;
import sprint.server.service.UserMatchService;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

@RestController
@RequiredArgsConstructor
@RequestMapping("/match/user/")
public class UserMatchApiController {

    private final UserMatchService userMatchService;
    private final MemberService memberService;
    private final StatisticsService statisticsService;
    @GetMapping("apply/{id}")
    public ApplyUserMatchResponse applyUserMatch(@PathVariable("id")Long memberID){
        memberService.findById(memberID);
        Calendar calendar = Calendar.getInstance();
        UserMatchApply applyResult = userMatchService.saveUserApplyMatchInfo(memberID,calendar);
        return new ApplyUserMatchResponse(applyResult.getMatchId());
    }


    @PostMapping("apply/test")
    public void test(){
        userMatchService.matchingApplyUser();
    }

    /**
     * 리그정보를 조회하는 API
     * @param memberId
     * @return
     */
    @GetMapping("apply/test/{id}")
    public LeagueInfoResponse viewLeagueInfo(@PathVariable("id")Long memberId){
        Member member = memberService.findById(memberId);

        /**
         * 리그 종료 (일요일 자정 12시)
         * 리그 종료 후 새벽에 일괄 계산후 리그 정산
         * 다음 리그 신청기간 (월요일~ 화요일)
         * 다음 리그 시작 (수요일부터)
         */
        //이번주의 시작인 월요일로 설정
        Calendar calendar =  statisticsService.getCalendarStart(new Timestamp(Calendar.getInstance().getTimeInMillis()), StatisticsType.Weekly);
        return new LeagueInfoResponse(userMatchService.viewLeagueInfo(member, new Timestamp(calendar.getTimeInMillis())));
    }

    @GetMapping("apply/test/save")
    public void save(){
        //이번주의 시작인 월요일로 설정
        Calendar calendar =  statisticsService.getCalendarStart(new Timestamp(Calendar.getInstance().getTimeInMillis()), StatisticsType.Weekly);

        userMatchService.finishLeague(new Timestamp(calendar.getTimeInMillis()));
    }


}
