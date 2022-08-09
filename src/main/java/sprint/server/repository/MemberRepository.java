package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByName(String name);
}
