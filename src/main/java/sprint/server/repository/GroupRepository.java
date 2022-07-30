package sprint.server.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.Groups;



@Repository
public interface GroupRepository extends JpaRepository<Groups,Long> {

}
