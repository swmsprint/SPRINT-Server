package sprint.server.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Member;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.FriendsRepository;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback(value = false)
public class FriendsServiceTest {

    @Autowired FriendsService friendsService;
    @Autowired MemberService memberService;

    @Test
    public void 친구관계테스트() throws Exception {
        //Given
        Member member1 = new Member();
        Member member2 = new Member();
        member1.setName("jeonghonn");
        member2.setName("yewon");

        memberService.join(member1);
        memberService.join(member2);

        //When
        Friends friends = friendsService.addFriends(member1, member2);
        System.out.println("friends = " + friends.toString());
        //Then

        assertEquals(friends.getSourceMemberId(),member1.getId());
        assertEquals(friends.getTargetMemberId(),member2.getId());

        }

}