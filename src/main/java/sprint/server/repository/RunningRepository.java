package sprint.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sprint.server.domain.Running;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class RunningRepository {

    private final EntityManager em;

    public void save(Running running){
        em.persist(running);
    }

    public Running findOne(Long id){
        return em.find(Running.class,id);
    }
    
}
