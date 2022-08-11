package sprint.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sprint.server.controller.datatransferobject.request.CreateMemberRequest;
import sprint.server.controller.datatransferobject.request.LoadMembersByNicknameRequest;
import sprint.server.controller.datatransferobject.request.ModifyMembersRequest;
import sprint.server.controller.datatransferobject.response.CreateMemberResponse;
import sprint.server.controller.datatransferobject.response.LoadMembersResponse;
import sprint.server.controller.datatransferobject.response.LoadMembersResponseDto;
import sprint.server.controller.datatransferobject.response.ModifyMembersResponse;
import sprint.server.domain.Member.Member;
import sprint.server.repository.MemberRepository;
import sprint.server.service.MemberService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

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
        List<Member> members = memberRepository.findByNicknameContaining(request.getNickname());
        List<LoadMembersResponseDto> result = members.stream()
                .map(member -> new LoadMembersResponseDto(member))
                .sorted(LoadMembersResponseDto.COMPARE_BY_NICKNAME)
                .collect(Collectors.toList());

        return new LoadMembersResponse(result.size(), result);
    }

    /**
     * 회원 정보 변경
     */
    @PutMapping("/api/members/modify")
    public ModifyMembersResponse ModifyMembers(@RequestBody @Valid ModifyMembersRequest request) {
        Boolean result = memberService.ModifyMembers(request);
        return new ModifyMembersResponse(result);
    }
}