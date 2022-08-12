package sprint.server.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.request.ModifyMembersRequest;
import sprint.server.controller.exception.ApiException;
import sprint.server.domain.member.Gender;
import sprint.server.domain.member.Member;
import sprint.server.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    /**
     * 회원가입 테스트
     * @throws Exception
     */
    @Test
    public void MemberJoinTest() throws Exception {
        /* 정상적인 요청 */
        Member member = new Member();
        String testName = "TestName";
        member.setNickname(testName);
        Long saveId = memberService.join(member);
        assertEquals(testName, memberService.findById(saveId).getNickname());

        /* 동일 닉네임이 이미 존재하는 경우 */
        Member member2 = new Member();
        member2.setNickname(testName);
        ApiException thrown = assertThrows(ApiException.class, () -> memberService.join(member2));
        assertEquals("M0002", thrown.getErrorCode());

        /* 동일 이메일이 존재하는 경우 */
        Member member3 = new Member();
        member3.setNickname("TestName2");
        member3.setEmail("test1@sprint.com");
        ApiException thrown2 = assertThrows(ApiException.class, () -> memberService.join(member3));
        assertEquals("M0003", thrown2.getErrorCode());
    }

    /**
     * 회원 정보 수정 테스트
     * @throws Exception
     */
    @Test
    public void ModifyMembersTest() throws Exception {
        /* 정상적인 요청 */
        ModifyMembersRequest modifyMembersRequest = new ModifyMembersRequest();
        modifyMembersRequest.setId(1L);
        modifyMembersRequest.setNickname("Modify1");
        modifyMembersRequest.setEmail("Modify@test.com");
        modifyMembersRequest.setGender(Gender.MALE);
        modifyMembersRequest.setBirthDay(LocalDate.of(2022, 03, 11));
        modifyMembersRequest.setHeight(166.7F);
        modifyMembersRequest.setWeight(70F);
        modifyMembersRequest.setPicture("modify@mtest.com");
        Boolean result = memberService.ModifyMembers(modifyMembersRequest);
        assertEquals(true, result);
        Member member = memberRepository.findById(1L).get();
        assertEquals(1L, member.getId());
        assertEquals("Modify1", member.getNickname());
        assertEquals("Modify@test.com", member.getEmail());
        assertEquals(Gender.MALE, member.getGender());
        assertEquals(LocalDate.of(2022, 03, 11), member.getBirthDay());
        assertEquals(166.7F, member.getHeight());
        assertEquals(70F, member.getWeight());
        assertEquals("modify@mtest.com", member.getPicture());

        /* 해당 회원이 존재하지 않을 때 */
        modifyMembersRequest.setId(0L);
        ApiException thrown = assertThrows(ApiException.class, ()->memberService.ModifyMembers(modifyMembersRequest));
        assertEquals("M0001", thrown.getErrorCode());
    }

    @Test
    public void findByNicknameContainingTest() throws Exception {
        /* 정상적인 요청 */
        List<Member> members = memberService.findByNicknameContaining("Test1");
        assertEquals(1, members.size());
        List<Member> members2 = memberService.findByNicknameContaining("Test");
        assertEquals(5, members2.size());
    }
}