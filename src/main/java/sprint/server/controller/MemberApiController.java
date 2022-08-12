package sprint.server.controller;

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
public class MemberApiController {
    private final MemberService memberService;

    /**
     * 회원 가입
     * @param request
     * @return
     */
    @PostMapping("/api/members")
    public CreateMemberResponse saveMember(@RequestBody @Valid CreateMemberRequest request){
        Member member = Member.createMember(request.getNickname(), request.getGender(), request.getEmail(), request.getBirthDay(), request.getHeight(), request.getWeight(), request.getPicture());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 회원 조회 (by Nickname, 사전순 출력)
     */
    @GetMapping("/api/members/list")
    public LoadMembersResponse<LoadMembersResponseDto> LoadMembersByNickname(@RequestBody @Valid LoadMembersByNicknameRequest request){
        List<Member> members = memberService.findByNicknameContaining(request.getNickname());
        List<LoadMembersResponseDto> result = members.stream()
                .map(LoadMembersResponseDto::new)
                .sorted(LoadMembersResponseDto.COMPARE_BY_NICKNAME)
                .collect(Collectors.toList());

        return new LoadMembersResponse(result.size(), result);
    }

    /**
     * 회원 비활성화
     */
    @PutMapping("/api/members/disable")
    public BooleanResponse DisableMember(@RequestBody @Valid OneMemberRequest request) {
        return new BooleanResponse(memberService.disableMember(request.getUserId()));
    }

    /**
     * 회원 활성화
     */
    @PutMapping("/api/members/enable")
    public BooleanResponse EnableMember(@RequestBody @Valid OneMemberRequest request) {
        return new BooleanResponse(memberService.enableMember(request.getUserId()));
    }

    /**
     * 회원 정보 변경
     */
    @PutMapping("/api/members/modify")
    public BooleanResponse ModifyMembers(@RequestBody @Valid ModifyMembersRequest request) {
        return new BooleanResponse(memberService.ModifyMembers(request));
    }

    /**
     * 중복 닉네임 확인
     * return (true -> 존재하지 않음, false -> 존재함)
     */
    @GetMapping ("/api/members/validation_duplicate_name")
    public BooleanResponse ValidationDuplicateNickname(@RequestBody @Valid ValidationDuplicateNicknameRequest request) {
        return new BooleanResponse(!memberService.IsExistsByNickname(request.getNickname()));
    }

    /**
     * 중복 이메일 확인
     * return (true -> 존재하지 않음, false -> 존재함)
     */
    @GetMapping ("/api/members/validation_duplicate_email")
    public BooleanResponse ValidationDuplicateEmail(@RequestBody @Valid ValidationDuplicateEmailRequest request) {
        return new BooleanResponse(!memberService.IsExistsByEmail(request.getEmail()));
    }
}