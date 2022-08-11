package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.request.ModifyMembersRequest;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.Member.Member;
import sprint.server.repository.MemberRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional // readOnly = false
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    public Member findById(Long id){
        return memberRepository.findById(id).get();
    }

    public void isMemberExistById(Long sourceMemberId, String message) {
        if (!memberRepository.existsById(sourceMemberId)) {
            throw new IllegalStateException(message);
        }
    }
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByNickname(member.getNickname());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 닉네임 입니다.");
        }
    }

    @Transactional
    public Boolean ModifyMembers(ModifyMembersRequest request) {
        Optional<Member> member = memberRepository.findById(request.getId());
        if (member.isEmpty()) {
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
        member.get().setNickname(request.getNickname());
        member.get().setEmail(request.getEmail());
        member.get().setGender(request.getGender());
        member.get().setBirthDay(request.getBirthDay());
        member.get().setHeight(request.getHeight());
        member.get().setWeight(request.getWeight());
        member.get().setPicture(request.getPicture());
        return true;
    }
}
