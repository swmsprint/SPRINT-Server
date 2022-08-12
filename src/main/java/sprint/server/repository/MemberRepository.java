package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.member.Member;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    List<Member> findByNickname(String Nickname);
    List<Member> findByNicknameContaining(String Nickname);
    Boolean existsByNickname(String Nickname);
    Boolean existsByEmail(String email);
}
