package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.*;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.friend.FriendState;
import sprint.server.domain.member.Member;
import sprint.server.service.FriendService;
import sprint.server.service.MemberService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-management/user")
public class UserApiController {
    private final MemberService memberService;
    private final FriendService friendService;

    @ApiOperation(value="회원가입")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("")
    public CreateMemberResponse saveMember(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member(request.getNickname(), request.getGender(), request.getBirthday(), request.getHeight(), request.getWeight(), request.getPicture());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @ApiOperation(value="회원 목록 검색", notes="닉네임 기준, LIKE 연산\nExample: http://localhost:8080/user-management/userId=1&target=test")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("")
    public FindMembersResponseDto<FindMembersResponseVo> findMembersByNickname(@RequestParam Long userId, @RequestParam String target){
        Member member = memberService.findById(userId);
        List<Long> friendsIdList = friendService.findFriendsByMemberId(member, FriendState.ACCEPT).stream()
                .map(Member::getId).collect(Collectors.toList());
        List<Long> requestIdList = friendService.findBySourceMemberIdAndFriendState(member, FriendState.REQUEST).stream()
                .map(Member::getId).collect(Collectors.toList());
        List<Long> receiveIdList = friendService.findByTargetMemberIdAndFriendState(member, FriendState.REQUEST).stream()
                .map(Member::getId).collect(Collectors.toList());
        List<Member> members = memberService.findByNicknameContaining(target);
        List<FindMembersResponseVo> result = members.stream()
                .map(m -> new FindMembersResponseVo(m, friendsIdList, requestIdList, receiveIdList))
                .filter(m -> !m.getUserId().equals(userId))
                .sorted(FindMembersResponseVo.COMPARE_BY_NICKNAME)
                .collect(Collectors.toList());
        return new FindMembersResponseDto(result.size(), result);
    }

    @ApiOperation(value="회원 비활성화")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @DeleteMapping("/{userId}/disable")
    public BooleanResponse disableMember(@PathVariable Long userId) {
        Member member = memberService.findById(userId);
        return new BooleanResponse(memberService.disableMember(member));
    }

    @ApiOperation(value="회원 정보 변경")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PutMapping("/{userId}")
    public BooleanResponse modifyMembers(@PathVariable Long userId, @RequestBody @Valid MemberInfoDto request) {
        Member member = memberService.findById(userId);
        if (!member.getNickname().equals(request.getNickname()) && memberService.existsByNickname(request.getNickname())) throw new ApiException(ExceptionEnum.MEMBER_DUPLICATE_NICKNAME);
        return new BooleanResponse(memberService.modifyMembers(member, request));
    }
    @ApiOperation(value="멤버 정보")
    @GetMapping("/{userId}")
    public MemberInfoDto getMemberInfo(@PathVariable Long userId) {
        Member member = memberService.findById(userId);
        return new MemberInfoDto(member);
    }

    @ApiOperation(value="중복 닉네임 확인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping ("/validation-duplicate-name")
    public BooleanResponse validationDuplicateNickname(@RequestParam String target) {
        return new BooleanResponse(!memberService.existsByNickname(target));
    }
}