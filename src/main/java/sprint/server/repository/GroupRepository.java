package sprint.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sprint.server.domain.Groups;

import javax.persistence.EntityManager;


@Repository
@RequiredArgsConstructor
public class GroupRepository {
    private final EntityManager em;

    public void save(Groups groups){
        em.persist(groups);
    }
    public Groups findOne(Long id){
        return em.find(Groups.class, id);
    }
}
