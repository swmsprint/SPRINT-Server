package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.controller.datatransferobject.request.CreateReportRequest;
import sprint.server.controller.datatransferobject.response.BooleanResponse;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.member.Member;
import sprint.server.service.BlockService;
import sprint.server.service.MemberService;
import sprint.server.service.ReportService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-management")
public class ReportApiController {
    private final MemberService memberService;
    private final ReportService reportService;
    private final BlockService blockService;

    @ApiOperation(value="유저 신고")
    @PostMapping("/report")
    public BooleanResponse createReport(@RequestBody @Valid CreateReportRequest request) {
        Member sourceMember = memberService.findById(request.getSourceUserId());
        Member targetMember = memberService.findById(request.getTargetUserId());
        reportService.join(sourceMember, targetMember, request.getMessage());

        // 내 차단목록에 등록
        if (!blockService.alreadyBlockCheck(sourceMember.getId(), targetMember.getId()))
            blockService.requestBlock(sourceMember, targetMember);

        // 최근 10분 신고개수 확인
        Long countReport = reportService.countLast10minReport(targetMember);
        if (countReport >= 3) {
            memberService.disableMember(targetMember);
            blockService.globalBlockMemberJoin(targetMember);
        }
        return new BooleanResponse(true);
    }
}
