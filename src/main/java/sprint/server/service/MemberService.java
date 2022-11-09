package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.request.MemberInfoDto;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.member.Member;
import sprint.server.domain.member.ProviderPK;
import sprint.server.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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
        member.changeMemberInfo(request.getNickname(), request.getGender(), request.getBirthday(), request.getHeight(), request.getWeight(), request.getPicture());
        return true;
    }

    @Transactional
    public Boolean disableMember(Member member) {
        member.disable();
        return member.getDisableDay() != null;
    }

    @Transactional
    public Boolean activateMember(Member member){
        member.enable();
        return member.getDisableDay()==null;
    }

    public Member findById(Long id){
        Optional<Member> member = memberRepository.findByIdAndDisableDayIsNull(id);
        if (member.isPresent()) {
            return member.get();
        } else {
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
