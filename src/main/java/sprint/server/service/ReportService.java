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
        log.info("{} -> {} 회원 신고 등록 요청", sourceMember.getId(), targetMember.getId());
        Report report = new Report(sourceMember.getId(), targetMember.getId(), message);
        if (sourceMember.equals(targetMember)) {
            log.error("{} -> {} 본인 신고 등록 불가", sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.REPORT_SELF);
        }
        if (!validationNewReport(report)) {
            log.error("{} -> {} 특정 유저에 대한 신고는 하루에 한번 가능", sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.REPORT_ONCE_A_DAY);
        }
        reportRepository.save(report);
        log.info("{} -> {} 회원 신고 등록 요청 완료", sourceMember.getId(), targetMember.getId());
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
        Long count = reportRepository.countReportByTargetMemberIdLast10min(member.getId());
        log.info("ID : {}, 회원 최근 10분 신고정보 요청 : {}개", member.getId(), count);
        return count;
    }
}
