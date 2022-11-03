package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.usermatch.UserMatch;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {

    Optional<UserMatch> findAllByMemberIdAndMatchTimeBetween(Long memberId, Timestamp startTime, Timestamp endTime);

    List<UserMatch> findAllByTeamNumberAndMatchTimeBetween(Integer teamNumber, Timestamp startTime, Timestamp endTime);

    List<UserMatch> findAllByMatchTimeAfterOrderByMatchIdDesc(Timestamp startTime);

}

