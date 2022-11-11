package sprint.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.request.MemberInfoDto;
import sprint.server.controller.datatransferobject.response.BooleanResponse;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.member.Member;
import sprint.server.domain.member.ProviderPK;
import sprint.server.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional // readOnly = false
    public Long join(Member member){
        if (existsByProviderPK(member.getProviderPK())) throw new ApiException(ExceptionEnum.MEMBER_ALREADY_SIGNUP);
        memberRepository.save(member);
        return member.getId();
    }

    @Transactional
    public Boolean modifyMembers(Member member, MemberInfoDto request) {
        if(existsByNickname(request.getNickname())) {
            log.error("동일 이름 회원이 존재합니다.");
            throw new ApiException(ExceptionEnum.MEMBER_DUPLICATE_NICKNAME);
        }
        member.changeMemberInfo(request.getNickname(), request.getGender(), request.getBirthday(), request.getHeight(), request.getWeight(), request.getPicture());
        return true;
    }

    @Transactional
    public Boolean disableMember(Member member) {
        log.info("ID : {}, 비활성화 요청", member.getId());
        member.disable();
        log.info("ID : {}, 비활성화 완료", member.getId());
        return member.getDisableDay() != null;
    }

    @Transactional
    public Boolean activateMember(Member member){
        member.enable();
        return member.getDisableDay()==null;
    }

    public Member findById(Long id){
        log.info("ID : {}, 유저 검색", id);
        Optional<Member> member = memberRepository.findByIdAndDisableDayIsNull(id);
        if (member.isPresent()) {
            log.info("ID : {}, 유저 검색 완료", id);
            return member.get();
        } else {
            log.error("ID : {}, 유저 검색 실패", id);
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
    }

    // Scan whether disabled or not.
    public Member findByIdWhetherDisable(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            return member.get();
        } else {
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
    }
    public boolean existById(Long memberId) {
        return memberRepository.existsByIdAndDisableDayIsNull(memberId);
    }
    public List<Member> findByNicknameContaining(String nickname) {
        return memberRepository.findByNicknameContainingAndDisableDayIsNull(nickname);
    }
    public boolean existsByNickname(String nickname) {
        if(nickname.equals("default")) return true;
        return memberRepository.existsByNickname(nickname);
    }

    public boolean existsByProviderPK(ProviderPK providerPK) {
        return memberRepository.existsByProviderPK(providerPK);
    }

    public Member findByProviderPK(ProviderPK providerPK) {
        return memberRepository.findByProviderPK(providerPK)
                .orElse(null);
    }

    public void deleteMember(Member member) {
        member.deleteMemberInfo();
    }
}
