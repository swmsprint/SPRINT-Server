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
        Member member1 = new Member();
        Member member2 = new Member();
        member1.setNickname("Test1");
        member1.setEmail("test1@sprint.com");
        member1.setBirthDay(LocalDate.of(2011, 02, 28));
        member1.setGender(Gender.FEMALE);
        member1.setHeight(180.0f);
        member1.setWeight(70f);
        member1.setTierId(0);

        member2.setNickname("Test2");
        member2.setGender(Gender.MALE);
        member2.setEmail("test2@sprint.com");
        member2.setBirthDay(LocalDate.of(2001, 02, 11));
        member2.setHeight(180.0f);
        member2.setWeight(70f);
        member2.setTierId(0);
        memberService.join(member1);
        memberService.join(member2);
    }
}