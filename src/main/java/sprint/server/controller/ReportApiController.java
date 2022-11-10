package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.controller.datatransferobject.request.CreateReportRequest;
import sprint.server.controller.datatransferobject.response.BooleanResponse;
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
        Member sourceMember = memberService.findById(request.getSourceMemberId());
        Member targetMember = memberService.findById(request.getTargetMemberId());
        reportService.join(sourceMember, targetMember, request.getMessage());

        Long countReport = reportService.countLast10minReport(targetMember);
        if (countReport >= 3) {
            memberService.disableMember(targetMember);
            blockService.globalBlockMemberJoin(targetMember);
        }
        return new BooleanResponse(true);
    }
}
