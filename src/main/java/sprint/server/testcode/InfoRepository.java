package sprint.server.testcode;

import org.springframework.stereotype.Repository;
import sprint.server.testcode.Info;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class InfoRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Info info){
        em.persist(info);
        return info.getId();
    }

    public Info find(Long id){
        return em.find(Info.class,id);
    }
}
