package sprint.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.controller.datatransferobject.request.CreateRunningRequest;
import sprint.server.controller.datatransferobject.request.FinishRunningRequest;
import sprint.server.domain.Running;
import sprint.server.domain.friend.FriendState;
import sprint.server.domain.member.Member;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.service.FriendService;
import sprint.server.service.MemberService;
import sprint.server.service.RunningService;
import sprint.server.service.StatisticsService;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/running/")
public class RunningApiController {

    private final RunningService runningService;
    private final MemberService memberService;
    private final StatisticsService statisticsService;
    private final FriendService friendService;


    @ApiOperation(value="개발자용/러닝 시작", notes = "성공시 저장된 runningId를 반환합니다")
    @PostMapping("developer/start")
    public CreateRunningResponse developerCreateRunning(@RequestBody @Valid CreateRunningRequest request) {
        Member member = memberService.findById(request.getUserId());
        Long runningId = runningService.addRun(member,request.getStartTime());
        return new CreateRunningResponse(runningId);
    }
    @ApiOperation(value="개발자용/러닝 종료", notes = "성공시 저장및 계산된 running 정보를 반환합니다. 통계정보가 존재하지 않는다면 통계정보도 생성 및 업데이트 시켜줍니다")
    @PostMapping("developer/finish")
    public FinishRunningResponse developFinishRunning(@RequestBody @Valid FinishRunningRequest request) throws JsonProcessingException {
        Running running = runningService.finishRunning(request);
        statisticsService.updateStatistics(running, StatisticsType.Daily);
        statisticsService.updateStatistics(running, StatisticsType.Weekly);
        statisticsService.updateStatistics(running, StatisticsType.Monthly);
        statisticsService.updateStatistics(running, StatisticsType.Yearly);
        return new FinishRunningResponse(running.getId(),running.getDistance(),running.getDuration(),running.getEnergy());
    }

    @ApiOperation(value="러닝 시작", notes = "성공시 저장된 runningId를 반환합니다")
    @PostMapping("start")
    public CreateRunningResponse createRunning(@RequestBody @Valid CreateRunningRequest request) {
        Member member = memberService.findById(request.getUserId());
        Long runningId = runningService.addRun(member,request.getStartTime());
        return new CreateRunningResponse(runningId);
    }

    @ApiOperation(value="러닝 종료", notes = "성공시 저장및 계산된 running 정보를 반환합니다")
    @PostMapping("finish")
    public FinishRunningResponse finishRunning(@RequestBody @Valid FinishRunningRequest request) throws JsonProcessingException {
        Running running = runningService.finishRunning(request);
        statisticsService.updateStatistics(running, StatisticsType.Daily);
        return new FinishRunningResponse(running.getId(),running.getDistance(),running.getDuration(),running.getEnergy());
    }


    @ApiOperation(value="러닝 정보 반환", notes = "성공시 저장된 running 정보의 자세한 정보들을 반환합니다")
    @GetMapping("detail")
    public ViewRunningResponse viewRunningDetail(@RequestParam(value="runningId")Long runningId,
                                                 @RequestParam(value="userId")Long memberId )throws JsonProcessingException{
        Running running = runningService.findOne(runningId).get();
        /**
         * 아직 러닝 정보 공개 정책이 없기때문에 전부 받아서 반환해줌 -> 추후 수정 필요
         */

        return new ViewRunningResponse(running.getId(),running.getDistance(),
                running.getDuration(),running.getEnergy(),
                running.getRunningRawDataList());
    }


    @ApiOperation(value="유저의 최근 러닝 3개 리스트 반환", notes = "성공시 저장된 3개의 running 정보를 반환합니다")
    @GetMapping("personal")
    public List<PersonalRunningInfoDTO> viewPersonalRecentRunningList(@RequestParam(value="userId")Long memberId,
                                                                      @RequestParam(value="pageNumber") Integer pageNumber){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Member member = memberService.findById(memberId);
        return runningService.fetchPersonalRunningPages(pageNumber,member).toList().stream()
                .map(running -> new PersonalRunningInfoDTO(running.getId(),running.getDuration(),running.getDistance(),dateFormat.format(running.getStartTime()),running.getEnergy()))
                .collect(java.util.stream.Collectors.toList());
    }


    @ApiOperation(value="유저 및 친구의 최근 러닝 3개 리스트 반환", notes = "성공시 저장된 3개의 running 정보를 반환합니다")
    @GetMapping("public")
    public List<PublicRunningInfoDTO> viewPublicRecentRunningList(@RequestParam(value="userId")Long memberId,
                                                      @RequestParam(value="pageNumber") Integer pageNumber){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Member member = memberService.findById(memberId);
        List<Member> relationMembers = friendService.findFriendsByMemberId(member, FriendState.ACCEPT);
        relationMembers.add(member);
        return runningService.fetchPublicRunningPages(pageNumber,relationMembers).toList().stream()
                .map(running -> new PublicRunningInfoDTO(running.getId(),running.getMember().getId(),running.getDuration(),running.getDistance(),dateFormat.format(running.getStartTime()),running.getEnergy()))
                .collect(java.util.stream.Collectors.toList());
    }


}
