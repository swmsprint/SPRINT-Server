package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sprint.server.domain.Report;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    /**
     * @param targetMemberId USER_ID
     * @return 최근 10분간 받은 신고내역 개수
     */
    @Query(value = "select count(1) from report where report.target_member_id = :targetMemberId and created_date > DATE_SUB(NOW(), INTERVAL 10 MINUTE)", nativeQuery = true)
    Long countReportByTargetMemberIdLast10min(@Param("targetMemberId")Long targetMemberId);

    @Query(value = "select * from report where report.source_member_id = :sourceMemberId and " +
            "report.target_member_id = :targetMemberId and " +
            "created_date > DATE_SUB(NOW(), INTERVAL 1 DAY)", nativeQuery = true)
    Optional<Report> findReportLastDay(@Param("sourceMemberId")Long sourceMemberId, @Param("targetMemberId") Long targetId);
}
