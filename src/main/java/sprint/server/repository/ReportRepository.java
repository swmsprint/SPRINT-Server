package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sprint.server.domain.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    /**
     * @param targetMemberId USER_PK
     * @return 최근 10분간 받은 신고내역 개수
     */
    @Query(value = "select count(1) from report where report.target_member_id = :targetMemberId and created_date > DATE_SUB(NOW(), INTERVAL 10 MINUTE)", nativeQuery = true)
    Long countReportByTargetMemberIdLast10min(@Param("targetMemberId")Long targetMemberId);
}
