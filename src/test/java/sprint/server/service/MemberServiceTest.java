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
        String testName = "TestName";
        String testName2 = "TestName2";
        /* 정상적인 요청 */
        Member member = Member.createMember(testName, Gender.FEMALE, testName + "@sprint.com", LocalDate.of(2011, 02, 28), 180.0f, 70f, null);
        member.setNickname(testName);
        Long saveId = memberService.join(member);
        assertEquals(testName, memberService.findById(saveId).getNickname());

        /* 동일 닉네임이 이미 존재하는 경우 */
        Member member2 = Member.createMember(testName, Gender.FEMALE, testName + "@sprint.com", LocalDate.of(2011, 02, 28), 180.0f, 70f, null);
        member2.setNickname(testName);
        ApiException thrown = assertThrows(ApiException.class, () -> memberService.join(member2));
        assertEquals("M0002", thrown.getErrorCode());

        /* 동일 이메일이 존재하는 경우 */
        Member member3 = Member.createMember(testName2, Gender.FEMALE, testName + "@sprint.com", LocalDate.of(2011, 02, 28), 180.0f, 70f, null);
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

    /**
     * 회원 정보 이름으로 검색 (LIKE) 테스트
     * @throws Exception
     */
    @Test
    public void findByNicknameContainingTest() throws Exception {
        /* 정상적인 요청 */
        List<Member> members = memberService.findByNicknameContaining("Test1");
        assertEquals(1, members.size());
        List<Member> members2 = memberService.findByNicknameContaining("Test");
        assertEquals(5, members2.size());
    }

    /**
     * 회원 정보 비활성화 테스트
     */
    @Test
    public void disableMemberTest() throws Exception {
        /* 정상적인 요청 */
        Boolean result = memberService.disableMember(1L);
        assertEquals(true, result);
        assertNotEquals(null, memberService.findById(1L).getDisableDay());
        assertNull(memberService.findById(2L).getDisableDay());

        /* 해당 유저가 없을때 */
        ApiException thrown = assertThrows(ApiException.class, () -> memberService.disableMember(-1L));
        assertEquals("M0001", thrown.getErrorCode());
    }
}