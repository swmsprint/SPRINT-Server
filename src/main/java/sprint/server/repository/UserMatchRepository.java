package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sprint.server.domain.usermatch.UserMatch;

public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {

}

