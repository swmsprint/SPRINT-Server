package sprint.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.controller.datatransferobject.request.CreateMemberRequest;
import sprint.server.controller.datatransferobject.response.CreateMemberResponse;
import sprint.server.domain.Member.Member;
import sprint.server.service.MemberService;

import javax.validation.Valid;
import java.sql.Timestamp;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    @PostMapping("/api/members")
    public CreateMemberResponse saveMember(@RequestBody @Valid CreateMemberRequest request){
        Member member = Member.createMember(request.getNickname(), request.getGender(), request.getEmail(), request.getBirthDay(), request.getHeight(), request.getWeight(), request.getPicture());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
}