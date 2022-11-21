package sprint.server.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.request.MemberInfoDto;
import sprint.server.controller.exception.ApiException;
import sprint.server.domain.member.Gender;
import sprint.server.domain.member.Member;
import sprint.server.domain.member.Provider;
import sprint.server.domain.member.ProviderPK;
import sprint.server.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    /**
     * 회원가입 테스트
     */
    @Test
    void memberJoinTest(){
        String testName = "TestName";
        String testName2 = "TestName2";
        /* 정상적인 요청 */
        Member member = new Member(null, new ProviderPK(Provider.KAKAO, "TESTT"));
        member.changeMemberInfo(testName, Gender.FEMALE,  LocalDate.of(2011, 2, 28), 180.0f, 70f, null);
        Long saveId = memberService.join(member);
        Member foundMember1 = memberService.findById(saveId);
        assertEquals(testName, foundMember1.getNickname());
        assertEquals(LocalDate.of(2011, 2, 28), foundMember1.getBirthday());
        assertEquals(180.0F, foundMember1.getHeight());
        assertEquals(70F,foundMember1.getWeight());

        /* 수정 예정 */
        /* 동일 닉네임이 이미 존재하는 경우 */
//        Member member2 = new Member(testName, Gender.FEMALE, LocalDate.of(2011, 2, 28), 180.0f, 70f, null);
//        ApiException thrown = assertThrows(ApiException.class, () -> memberService.join(member2));
//        assertEquals("M0002", thrown.getErrorCode());
    }

    /**
     * 회원 정보 수정 테스트
     */
    @Test
    void modifyMembersTest(){
        Member member = memberService.findById(1L);
        /* 정상적인 요청 */
        MemberInfoDto memberInfoDto = new MemberInfoDto();
        memberInfoDto.setNickname("Modify1");
        memberInfoDto.setGender(Gender.MALE);
        memberInfoDto.setBirthday(LocalDate.of(2022, 3, 11));
        memberInfoDto.setHeight(166.7F);
        memberInfoDto.setWeight(70F);
        memberInfoDto.setPicture("modify@mtest.com");
        Boolean result = memberService.modifyMembers(member, memberInfoDto);
        assertEquals(true, result);
        assertEquals(1L, member.getId());
        assertEquals("Modify1", member.getNickname());
        assertEquals(Gender.MALE, member.getGender());
        assertEquals(LocalDate.of(2022, 3, 11), member.getBirthday());
        assertEquals(166.7F, member.getHeight());
        assertEquals(70F, member.getWeight());
        assertEquals("modify@mtest.com", member.getPicture());
    }

    /**
     * 회원 정보 이름으로 검색 (LIKE) 테스트
     */
    @Test
    void findByNicknameContainingTest(){
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
    void disableMemberTest(){
        Member member = memberService.findById(1L);
        /* 정상적인 요청 */
        Boolean result = memberService.disableMember(member);
        assertEquals(true, result);
        ApiException thrown = assertThrows(ApiException.class, () -> memberService.findById(1L));
        assertEquals("M0001",thrown.getErrorCode());
        assertNull(memberService.findById(2L).getDisableDay());
    }
}