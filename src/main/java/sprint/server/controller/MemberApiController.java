package sprint.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.*;
import sprint.server.controller.datatransferobject.response.*;
import sprint.server.domain.member.Member;
import sprint.server.service.MemberService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-management")
public class MemberApiController {
    private final MemberService memberService;

    @ApiOperation(value="회원가입")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("/users")
    public CreateMemberResponse saveMember(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member(request.getNickname(), request.getGender(), request.getEmail(), request.getBirthday(), request.getHeight(), request.getWeight(), request.getPicture());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @ApiOperation(value="회원 목록 검색", notes="닉네임 기준, LIKE 연산\nExample: http://localhost:8080/user-management/users?target=test")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/users")
    public FindMembersResponseDto<FindMembersResponseVo> FindMembersByNickname(@RequestParam String target){
        List<Member> members = memberService.findByNicknameContaining(target);
        List<FindMembersResponseVo> result = members.stream()
                .map(FindMembersResponseVo::new)
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
    @PutMapping("/users/{userId}/disable")
    public BooleanResponse disableMember(@PathVariable Long userId) {
        return new BooleanResponse(memberService.disableMember(userId));
    }

    @ApiOperation(value="회원 활성화")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PutMapping("/users/{userId}/enable")
    public BooleanResponse enableMember(@PathVariable Long userId) {
        return new BooleanResponse(memberService.enableMember(userId));
    }

    @ApiOperation(value="회원 정보 변경")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PutMapping("/users/{userId}")
    public BooleanResponse modifyMembers(@PathVariable Long userId, @RequestBody @Valid ModifyMembersRequest request) {
        return new BooleanResponse(memberService.modifyMembers(userId, request));
    }

    @ApiOperation(value="중복 닉네임 확인", notes="Example: http://localhost:8080/user-management/users?target=test")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping ("/validation-duplicate-name")
    public BooleanResponse validationDuplicateNickname(@RequestParam String target) {
        return new BooleanResponse(!memberService.existsByNickname(target));
    }

    @ApiOperation(value="중복 이메일 확인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 작동"),
            @ApiResponse(code = 400, message = "요청 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping ("/validation-duplicate-email")
    public BooleanResponse validationDuplicateEmail(@RequestParam String target) {
        return new BooleanResponse(!memberService.existsByEmail(target));
    }
}