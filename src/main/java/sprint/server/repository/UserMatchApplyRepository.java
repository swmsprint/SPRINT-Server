package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.usermatch.UserMatchApply;
import sprint.server.domain.usermatch.UserMatchApplyId;


@Repository
public interface UserMatchApplyRepository extends JpaRepository<UserMatchApply, UserMatchApplyId> {

}
