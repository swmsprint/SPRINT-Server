package sprint.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Member.Gender;
import sprint.server.domain.Member.Member;
import sprint.server.service.MemberService;

import java.time.LocalDate;

@SpringBootTest
@Transactional
@Component
public class SaveTempMember {
    @Autowired
    MemberService memberService;

    @Bean
    public void Save2TestMember() {
        Member member1 = Member.createMember("Test1", Gender.FEMALE, "test1@sprint.com", LocalDate.of(2011, 02, 28), 180.0f, 70f, null);
        Member member2 = Member.createMember("Test2", Gender.FEMALE, "test2@sprint.com", LocalDate.of(2012, 02, 12), 166.0f, 65f, null);
        Member member3 = Member.createMember("Test3", Gender.FEMALE, "test3@sprint.com", LocalDate.of(2006, 11, 30), 180.0f, 70f, null);
        Member member4 = Member.createMember("Test4", Gender.FEMALE, "test4@sprint.com", LocalDate.of(2008, 10, 10), 166.0f, 65f, null);
        Member member5 = Member.createMember("Test5", Gender.FEMALE, "test5@sprint.com", LocalDate.of(2009, 04, 28), 180.0f, 70f, null);
        memberService.join(member1);
        memberService.join(member2);
        memberService.join(member3);
        memberService.join(member4);
        memberService.join(member5);
    }
}