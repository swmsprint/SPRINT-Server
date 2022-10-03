package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.usermatch.MatchStatus;
import sprint.server.domain.usermatch.UserMatchApply;
import sprint.server.domain.usermatch.UserMatchApplyId;

import java.util.List;


@Repository
public interface UserMatchApplyRepository extends JpaRepository<UserMatchApply, UserMatchApplyId> {

    Integer countByMatchStatus(MatchStatus matchStatus);
    List<UserMatchApply> findAllByMatchStatusOrderByScoreDesc(MatchStatus matchStatus);

}
