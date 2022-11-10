package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.controller.datatransferobject.request.CreateReportRequest;
import sprint.server.controller.datatransferobject.response.BooleanResponse;
import sprint.server.domain.member.Member;
import sprint.server.service.MemberService;
import sprint.server.service.ReportService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-management/report-user")
public class ReportApiController {
    private final MemberService memberService;
    private final ReportService reportService;

    @ApiOperation(value="유저 신고")
    @PostMapping("")
    public BooleanResponse createReport(@RequestBody @Valid CreateReportRequest request) {
        Member targetMember = memberService.findById(request.getTargetMemberId());
        reportService.join(targetMember, request.getMessage());

        Long countReport = reportService.countLast10minReport(targetMember);
        if (countReport >= 3) memberService.disableMember(targetMember);

        return new BooleanResponse(true);
    }
}
