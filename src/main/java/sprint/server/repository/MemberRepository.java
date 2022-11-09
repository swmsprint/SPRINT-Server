package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sprint.server.domain.member.Member;
import sprint.server.domain.member.ProviderPK;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByNicknameContainingAndDisableDayIsNull(String nickname);
    Optional<Member> findByIdAndDisableDayIsNull(Long id);
    Optional<Member> findByProviderPK(ProviderPK providerPK);
    boolean existsByNickname(String nickName);
    boolean existsByIdAndDisableDayIsNull(Long id);

    boolean existsByProviderPK(ProviderPK providerPK);

    @Query(value = "select * from member where DATE(disable_day) < DATE_SUB(NOW(), INTERVAL 60 DAY)", nativeQuery = true)
    List<Member> findDisableMembers();
}
