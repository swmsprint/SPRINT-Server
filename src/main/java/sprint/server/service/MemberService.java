package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Member;
import sprint.server.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member){
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
}
