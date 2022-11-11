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
import sprint.server.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockService {
    private final BlockRepository blockRepository;
    private final GlobalBlockRepository globalBlockRepository;
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void globalBlockMemberJoin(Member member) {
        log.info("ID : {}, global block 등록 요청", member.getId());
        Optional<GlobalBlock> foundMember = globalBlockRepository.findById(member.getId());
        if (foundMember.isPresent()) return;

        GlobalBlock globalBlock = new GlobalBlock(member.getId());
        log.info("ID : {}, global block 등록 완료", member.getId());
        globalBlockRepository.save(globalBlock);
    }

    @Transactional
    public boolean requestBlock(Member sourceMember, Member targetMember) {
        Long sourceMemberId = sourceMember.getId();
        Long targetMemberId = targetMember.getId();
        log.info("{} -> {}, 유저 차단 요청", sourceMemberId, targetMemberId);
        if (sourceMember.equals(targetMember)) {
            log.error("{} -> {}, 동일 유저 차단 불가", sourceMemberId, targetMemberId);
            throw new ApiException(ExceptionEnum.BLOCK_SELF);
        }

        // 이미 차단되었는지 확인.
        Optional<Block> block = blockRepository.findBlockBySourceMemberIdAndAndTargetMemberId(sourceMemberId, targetMemberId);
        if (block.isPresent()) {
            log.error("{} -> {}, 이미 차단 정보 존재", sourceMemberId, targetMemberId);
            throw new ApiException(ExceptionEnum.BLOCK_ALREADY);
        }

        // 만약 친구 관계가 존재한다면 삭제
        Optional<Friend> friend = friendRepository.findFriendByTwoMemberId(sourceMemberId, targetMemberId);
        if (friend.isPresent()){
            log.info("{} -> {}, 친구관계 삭제", sourceMemberId, targetMemberId);
            friendRepository.delete(friend.get());
        }

        Block newBlock = new Block(sourceMemberId, targetMemberId);
        log.info("{} -> {}, 유저 차단 완료", sourceMemberId, targetMemberId);
        blockRepository.save(newBlock);
        return true;
    }

    @Transactional
    public boolean requestUnblock(Member sourceMember, Member targetMember) {
        Long sourceMemberId = sourceMember.getId();
        Long targetMemberId = targetMember.getId();
        log.info("{} -> {}, 유저 차단 해제 요청", sourceMemberId, targetMemberId);
        if (sourceMember.equals(targetMember)) {
            log.error("{} -> {}, 동일 유저 에러", sourceMemberId, targetMemberId);
            throw new ApiException(ExceptionEnum.BLOCK_SELF);
        }

        // 기존 차단 내용을 확인
        Optional<Block> block = blockRepository.findBlockBySourceMemberIdAndAndTargetMemberId(sourceMemberId, targetMemberId);
        if (block.isEmpty()) {
            log.error("{} -> {}, 유저 차단 검색 실패", sourceMemberId, targetMemberId);
            throw new ApiException(ExceptionEnum.BLOCK_NOT_FOUND);
        }

        blockRepository.delete(block.get());
        log.info("{} -> {}, 유저 차단 해제 성공", sourceMemberId, targetMemberId);
        return true;
    }

    public boolean alreadyBlockCheck(Long sourceUserId, Long targetUserId) {
        Optional<Block> block = blockRepository.findBlockBySourceMemberIdAndAndTargetMemberId(sourceUserId, targetUserId);
        log.info("{} -> {} 이미 차단되어있는지 확인 : {}", sourceUserId, targetUserId, block.isPresent());
        return block.isPresent();
    }

    public List<Member> findBlockedMember(Member member) {
        log.info("ID : {}, 유저 차단 목록 요청", member.getId());
        List<Block> blockList = blockRepository.findBlockBySourceMemberId(member.getId());
        List<Member> blockMemberList = blockList.stream().map(block -> memberRepository.findById(block.getTargetMemberId()))
                .filter(block -> block.isPresent())
                .map(block -> block.get())
                .collect(Collectors.toList());
        return blockMemberList;
    }
}
