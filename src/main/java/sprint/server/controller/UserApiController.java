package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.*;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.friend.FriendState;
import sprint.server.domain.member.Member;
import sprint.server.service.BlockService;
import sprint.server.service.FriendService;
import sprint.server.service.MemberService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user-management/user")
public class UserApiController {
    private final MemberService memberService;
    private final FriendService friendService;
    private final BlockService blockService;

    @ApiOperation(value="회원가입")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("")
    public CreateMemberResponse saveMember(@RequestBody @Valid CreateMemberRequest request){
        log.info("회원가입");
        Member member = new Member(request.getNickname(), request.getGender(), request.getBirthday(), request.getHeight(), request.getWeight(), request.getPicture());
        Long id = memberService.join(member);
        log.info("ID: {}, 회원가입 완료", id);
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
        log.info("회원 검색");
        Member member = memberService.findById(userId);
        log.info("ID: {}, 회원 검색 요청", member.getId());
        List<Long> friendsIdList = friendService.findFriendsByMemberId(member, FriendState.ACCEPT).stream()
                .map(Member::getId).collect(Collectors.toList());
        List<Long> requestIdList = friendService.findBySourceMemberIdAndFriendState(member, FriendState.REQUEST).stream()
                .map(Member::getId).collect(Collectors.toList());
        List<Long> receiveIdList = friendService.findByTargetMemberIdAndFriendState(member, FriendState.REQUEST).stream()
                .map(Member::getId).collect(Collectors.toList());
        List<Member> members = memberService.findByNicknameContaining(target);
        List<Member> blockList = blockService.findBlockedMember(member);
        List<FindMembersResponseVo> result = members.stream()
                .filter(m -> !blockList.contains(m))
                .map(m -> new FindMembersResponseVo(m, friendsIdList, requestIdList, receiveIdList))
                .filter(m -> !m.getUserId().equals(userId))
                .sorted(FindMembersResponseVo.COMPARE_BY_NICKNAME)
                .collect(Collectors.toList());
        log.info("ID: {}, 회원 검색 완료, 결과 : {}개 발견", result.size());
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
        log.info("회원 비활성화");
        Member member = memberService.findById(userId);
        log.info("ID : {}, 회원 비활성화 요청", member.getId());
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
        log.info("회원 정보 변경");
        Member member = memberService.findByIdWhetherDisable(userId);
        memberService.activateMember(member);
        log.info("ID: {}, 회원 정보 변경 요청", member.getId());
        if (member.getNickname() != null && !member.getNickname().equals(request.getNickname()) && memberService.existsByNickname(request.getNickname())) throw new ApiException(ExceptionEnum.MEMBER_DUPLICATE_NICKNAME);
        return new BooleanResponse(memberService.modifyMembers(member, request));
    }

    @ApiOperation(value="멤버 정보")
    @GetMapping("/{userId}")
    public MemberInfoDto getMemberInfo(@PathVariable Long userId) {
        Member member = memberService.findById(userId);
        return new MemberInfoDto(member);
    }

    @ApiOperation(value="중복 닉네임 확인", notes="Example: http://localhost:8080/user-management/users?target=test")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping ("/validation-duplicate-name")
    public BooleanResponse validationDuplicateNickname(@RequestParam String target) {
        log.info("중복 닉네임 확인");
        return new BooleanResponse(!memberService.existsByNickname(target));
    }
}