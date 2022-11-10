package sprint.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.block.Block;
import sprint.server.domain.block.BlockId;
import sprint.server.domain.block.GlobalBlock;
import sprint.server.domain.friend.Friend;
import sprint.server.domain.member.Member;
import sprint.server.repository.BlockRepository;
import sprint.server.repository.FriendRepository;
import sprint.server.repository.GlobalBlockRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockService {
    private final BlockRepository blockRepository;
    private final GlobalBlockRepository globalBlockRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public void globalBlockMemberJoin(Member member) {
        Optional<GlobalBlock> foundMember = globalBlockRepository.findById(member.getId());
        if (foundMember.isPresent()) return;

        GlobalBlock globalBlock = new GlobalBlock(member.getId());
        globalBlockRepository.save(globalBlock);
    }

    @Transactional
    public boolean requestBlock(Member sourceMember, Member targetMember) {
        if (sourceMember.equals(targetMember)) throw new ApiException(ExceptionEnum.BLOCK_SELF);

        Long sourceMemberId = sourceMember.getId();
        Long targetMemberId = targetMember.getId();
        // 이미 차단되었는지 확인.
        Optional<Block> block = blockRepository.findBlockBySourceMemberIdAndAndTargetMemberId(sourceMemberId, targetMemberId);
        if (block.isPresent()) throw new ApiException(ExceptionEnum.BLOCK_ALREADY);


        Block newBlock = new Block(sourceMemberId, targetMemberId);
        blockRepository.save(newBlock);
        return true;
    }

    @Transactional
    public boolean requestUnblock(Member sourceMember, Member targetMember) {
        if (sourceMember.equals(targetMember)) throw new ApiException(ExceptionEnum.BLOCK_SELF);
        Long sourceMemberId = sourceMember.getId();
        Long targetMemberId = targetMember.getId();

        // 기존 차단 내용을 확인
        Optional<Block> block = blockRepository.findBlockBySourceMemberIdAndAndTargetMemberId(sourceMemberId, targetMemberId);
        if (block.isEmpty()) throw new ApiException(ExceptionEnum.BLOCK_NOT_FOUND);

        // 만약 친구 관계가 존재한다면 삭제
        Optional<Friend> friend = friendRepository.findFriendByTwoMemberId(sourceMemberId, targetMemberId);
        friend.ifPresent(friendRepository::delete);

        blockRepository.delete(block.get());
        return true;
    }

    public boolean alreadyBlockCheck(Long sourceUserId, Long targetUserId) {
        Optional<Block> block = blockRepository.findBlockBySourceMemberIdAndAndTargetMemberId(sourceUserId, targetUserId);
        return block.isPresent();
    }
}
