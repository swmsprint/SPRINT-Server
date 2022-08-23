package sprint.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.member.Gender;
import sprint.server.domain.member.Member;
import sprint.server.service.MemberService;

import java.time.LocalDate;

@SpringBootTest
@Transactional
@Component
public class SaveTempMember {
    @Autowired
    MemberService memberService;

    @Bean
    public void Save5TestMember() {
        Member member1 = new Member("Test1", Gender.FEMALE, "test1@sprint.com", LocalDate.of(2011, 2, 28), 180.0f, 70f, null);
        Member member2 = new Member("Test2", Gender.FEMALE, "test2@sprint.com", LocalDate.of(2012, 2, 12), 166.0f, 65f, null);
        Member member3 = new Member("Test3", Gender.FEMALE, "test3@sprint.com", LocalDate.of(2006, 11, 30), 180.0f, 70f, null);
        Member member4 = new Member("Test4", Gender.FEMALE, "test4@sprint.com", LocalDate.of(2008, 10, 10), 166.0f, 65f, null);
        Member member5 = new Member("Test5", Gender.FEMALE, "test5@sprint.com", LocalDate.of(2009, 4, 28), 180.0f, 70f, null);
        memberService.join(member1);
        memberService.join(member2);
        memberService.join(member3);
        memberService.join(member4);
        memberService.join(member5);
    }
}