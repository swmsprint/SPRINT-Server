package sprint.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sprint.server.domain.Running;
import sprint.server.domain.statistics.Statistics;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class StatisticsRepository {
    private final EntityManager em;

    public void save(Statistics statistics){
        em.persist(statistics);
    }
    public Statistics findOne(Long id){
        return em.find(Statistics.class, id);
    }

}
