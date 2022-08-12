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

    @PutMapping("/api/members/disable")
    public DisableMemberResponse DisableMember(@RequestBody @Valid DisableMemberRequest request) {
        return new DisableMemberResponse(memberService.disableMember(request.getUserId()));
    }

    /**
     * 회원 정보 변경
     */
    @PutMapping("/api/members/modify")
    public ModifyMembersResponse ModifyMembers(@RequestBody @Valid ModifyMembersRequest request) {
        return new ModifyMembersResponse(memberService.ModifyMembers(request));
    }

    /**
     * 중복 닉네임 확인
     * return (true -> 존재하지 않음, false -> 존재함)
     */
    @GetMapping ("/api/members/validation_duplicate_name")
    public ValidationDuplicateResponse ValidationDuplicateNickname(@RequestBody @Valid ValidationDuplicateNicknameRequest request) {
        return new ValidationDuplicateResponse(!memberService.IsExistsByNickname(request.getNickname()));
    }

    /**
     * 중복 이메일 확인
     * return (true -> 존재하지 않음, false -> 존재함)
     */
    @GetMapping ("/api/members/validation_duplicate_email")
    public ValidationDuplicateResponse ValidationDuplicateEmail(@RequestBody @Valid ValidationDuplicateEmailRequest request) {
        return new ValidationDuplicateResponse(!memberService.IsExistsByEmail(request.getEmail()));
    }
}