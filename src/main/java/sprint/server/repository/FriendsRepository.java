package sprint.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sprint.server.domain.Member;
import sprint.server.domain.friends.Friends;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class FriendsRepository {
    private final EntityManager em;

    public void save(Friends friends){
        em.persist(friends);
    }

/*
    public Member findRelationship(Long sourceMemberId){
        return em.find(Friends.class,);
    } //jpa가 단건을 조회하는 로직
*/

}
