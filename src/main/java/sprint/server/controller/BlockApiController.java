package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.TwoMemberRequest;
import sprint.server.controller.datatransferobject.response.BooleanResponse;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.block.Block;
import sprint.server.domain.member.Member;
import sprint.server.service.BlockService;
import sprint.server.service.MemberService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-management/block")
public class BlockApiController {
    private final MemberService memberService;
    private final BlockService blockService;

    @ApiOperation(value = "유저 차단")
    @PostMapping("")
    public BooleanResponse createBlock(@RequestBody @Valid TwoMemberRequest request) {
        Member sourceMember = memberService.findById(request.getSourceUserId());
        Member targetMember = memberService.findById(request.getTargetUserId());
        return new BooleanResponse(blockService.requestBlock(sourceMember, targetMember));
    }

    @ApiOperation(value = "유저 차단 해제")
    @DeleteMapping("")
    public BooleanResponse createUnblock(@RequestBody @Valid TwoMemberRequest request) {
        Member sourceMember = memberService.findById(request.getSourceUserId());
        Member targetMember = memberService.findById(request.getTargetUserId());
        return new BooleanResponse(blockService.requestUnblock(sourceMember, targetMember));
    }
}
