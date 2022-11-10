package sprint.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.Report;
import sprint.server.domain.member.Member;
import sprint.server.repository.ReportRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;

    @Transactional
    public Long join(Member sourceMember, Member targetMember, String message) {
        Report report = new Report(sourceMember.getId(), targetMember.getId(), message);
        if (sourceMember.equals(targetMember)) throw new ApiException(ExceptionEnum.REPORT_SELF);
        if (!validationNewReport(report)) throw new ApiException(ExceptionEnum.REPORT_ONCE_A_DAY);
        reportRepository.save(report);
        return report.getId();
    }

    private boolean validationNewReport(Report report) {
        Long sourceId = report.getSourceMemberId();
        Long targetId = report.getTargetMemberId();
        Optional<Report> foundReport = reportRepository.findReportLastDay(sourceId, targetId);
        if (foundReport.isPresent()) return false;
        return true;
    }

    public Long countLast10minReport(Member member) {
        return reportRepository.countReportByTargetMemberIdLast10min(member.getId());
    }
}
