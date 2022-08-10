package sprint.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Member;
import sprint.server.service.MemberService;

@SpringBootTest
@Transactional
@Component
public class SaveTempMember {
    /**
     * 테스트 시작 시 임시로 5개의 멤버를 만듭니다.
     */
    @Autowired
    MemberService memberService;
    @Bean
    public void Save2TestMember() {
        Member member1 = MemberMaker("Test1", "test1@sprint.com", 180F, 75.6F, 0);
        Member member2 = MemberMaker("Test2", "test2@sprint.com", 176F, 71.6F, 1);
        Member member3 = MemberMaker("Test3", "test3@sprint.com", 162F, 80.6F, 2);
        Member member4 = MemberMaker("Test4", "test4@sprint.com", 165F, 86.6F, 3);
        Member member5 = MemberMaker("Test5", "test5@sprint.com", 179F, 74.6F, 4);

        memberService.join(member1); memberService.join(member2); memberService.join(member3); memberService.join(member4); memberService.join(member5);
    }

    private static Member MemberMaker(String Name, String email, float Height, float Weight, int TierId) {
        Member member= new Member();
        member.setName(Name);
        member.setEmail(email);
        member.setHeight(Height);
        member.setWeight(Weight);
        member.setTierId(TierId);
        return member;
    }
}