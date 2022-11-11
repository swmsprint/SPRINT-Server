package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.TwoMemberRequest;
import sprint.server.controller.datatransferobject.response.BooleanResponse;
import sprint.server.controller.datatransferobject.response.FindBlockResponseVo;
import sprint.server.controller.datatransferobject.response.FindMembersResponseDto;
import sprint.server.domain.member.Member;
import sprint.server.service.BlockService;
import sprint.server.service.MemberService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user-management/block")
public class BlockApiController {
    private final MemberService memberService;
    private final BlockService blockService;

    @ApiOperation(value = "유저 차단")
    @PostMapping("")
    public BooleanResponse createBlock(@RequestBody @Valid TwoMemberRequest request) {
        log.info("유저 차단");
        Member sourceMember = memberService.findById(request.getSourceUserId());
        Member targetMember = memberService.findById(request.getTargetUserId());
        return new BooleanResponse(blockService.requestBlock(sourceMember, targetMember));
    }

    @ApiOperation(value = "유저 차단 해제")
    @DeleteMapping("")
    public BooleanResponse createUnblock(@RequestBody @Valid TwoMemberRequest request) {
        log.info("유저 차단 해제");
        Member sourceMember = memberService.findById(request.getSourceUserId());
        Member targetMember = memberService.findById(request.getTargetUserId());
        return new BooleanResponse(blockService.requestUnblock(sourceMember, targetMember));
    }

    @ApiOperation(value = "차단 목록 확인")
    @GetMapping("/{userId}")
    public FindMembersResponseDto<FindBlockResponseVo> findBlockList(@PathVariable Long userId) {
        log.info("유저 차단 목록 확인");
        Member member = memberService.findById(userId);
        List<Member> blockedMember = blockService.findBlockedMember(member);
        List<FindBlockResponseVo> result = blockedMember.stream()
                .map(FindBlockResponseVo::new)
                .collect(Collectors.toList());
        log.info("ID : {},, 차단 목록 확인 결과 : {}",member.getId(), result.size());
        return new FindMembersResponseDto(result.size(), result);
    }
}
