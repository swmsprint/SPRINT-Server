package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.member.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByNicknameContainingAndDisableDayIsNull(String Nickname);
    Optional<Member> findByIdAndDisableDayIsNull(Long id);
    boolean existsByNicknameAndDisableDayIsNull(String Nickname);
    boolean existsByEmailAndDisableDayIsNull(String email);
    boolean existsByIdAndDisableDayIsNull(Long id);
}
