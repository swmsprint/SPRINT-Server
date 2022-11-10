package sprint.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Report;
import sprint.server.domain.member.Member;
import sprint.server.repository.ReportRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;

    @Transactional
    public Long join(Member member, String message) {
        Report report = new Report(member.getId(), message);
        reportRepository.save(report);
        return report.getId();
    }

    public Long countLast10minReport(Member member) {
        return reportRepository.countReportByTargetMemberIdLast10min(member.getId());
    }
}
