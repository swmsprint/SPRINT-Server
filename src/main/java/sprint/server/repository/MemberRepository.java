package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.member.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByNickname(String Nickname);
    List<Member> findByNicknameContaining(String Nickname);
    List<Member> findByNicknameContainingAndDisableDayIsNull(String Nickname);
    Optional<Member> findByIdAndDisableDayIsNull(Long id);
    List<Member> findALLByIdInAndDisableDayIsNull(List<Long> members);
    Boolean existsByNicknameAndDisableDayIsNull(String Nickname);
    Boolean existsByEmailAndDisableDayIsNull(String email);
    Boolean existsByIdAndDisableDayIsNull(Long id);
}
