package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.request.ModifyMembersRequest;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.member.Member;
import sprint.server.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional // readOnly = false
    public Long join(Member member){
        if (existsByNickname(member.getNickname())){
            throw new ApiException(ExceptionEnum.MEMBER_DUPLICATE_NICKNAME);
        } else if (existsByEmail(member.getEmail())){
            throw new ApiException(ExceptionEnum.MEMBER_DUPLICATE_EMAIL);
        } else {
            memberRepository.save(member);
            return member.getId();
        }
    }

    @Transactional
    public Boolean modifyMembers(Long userId, ModifyMembersRequest request) {
        Optional<Member> member = memberRepository.findByIdAndDisableDayIsNull(userId);
        if (member.isEmpty()) {
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
        member.get().changeMemberInfo(request.getNickname(), request.getGender(), request.getEmail(), request.getBirthday(), request.getHeight(), request.getWeight(), request.getPicture());
        return true;
    }

    @Transactional
    public Boolean disableMember(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        } else if (member.get().getDisableDay() != null) {
            throw new ApiException(ExceptionEnum.MEMBER_ALREADY_DISABLED);
        } else {
            member.get().setDisableDay(LocalDate.now());
            return true;
        }
    }

    @Transactional
    public Boolean enableMember(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        } else if (member.get().getDisableDay() == null){
            throw new ApiException(ExceptionEnum.MEMBER_NOT_DISABLED);
        } else {
            member.get().setDisableDay(null);
            return true;
        }
    }

    public Member findById(Long id){
        Optional<Member> member = memberRepository.findByIdAndDisableDayIsNull(id);
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
        return memberRepository.existsByNicknameAndDisableDayIsNull(nickname);
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmailAndDisableDayIsNull(email);
    }
}
