package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.member.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByNicknameContainingAndDisableDayIsNull(String nickName);
    Optional<Member> findByIdAndDisableDayIsNull(Long id);
    boolean existsByNicknameAndDisableDayIsNull(String nickName);
    boolean existsByIdAndDisableDayIsNull(Long id);
}
