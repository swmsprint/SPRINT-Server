package sprint.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.block.GlobalBlock;
import sprint.server.domain.member.Member;
import sprint.server.repository.GlobalBlockRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockService {
    private final GlobalBlockRepository globalBlockRepository;

    @Transactional
    public void globalBlockMemberJoin(Member member) {
        Optional<GlobalBlock> foundMember = globalBlockRepository.findById(member.getId());
        if (foundMember.isPresent()) return;

        GlobalBlock globalBlock = new GlobalBlock(member.getId());
        globalBlockRepository.save(globalBlock);
    }
}
