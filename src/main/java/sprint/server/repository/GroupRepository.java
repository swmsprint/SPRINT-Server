package sprint.server.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.Groups;

import java.util.List;
import java.util.Optional;


@Repository
public interface GroupRepository extends JpaRepository<Groups, Integer> {
    Boolean existsByGroupName(String groupName);

    List<Groups> findByGroupNameContaining(String groupName);
}
