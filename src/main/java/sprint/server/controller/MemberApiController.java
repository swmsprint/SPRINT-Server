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

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    @PostMapping("/api/members") //요청을 온걸 리퀘스트 바디로 받아,.
    public CreateMemberResponse saveMember(@RequestBody @Valid CreateMemberRequest request){
        //api 스팩이 안바뀜. 누가 username으로 변환해버리면,
        // 그냥 setName 을 setUserName으로 바꿔버리면 되기에 전혀 신경쓸필요 ㄴㄴ
        //즉 엔티티와 api 스펙을 분리해버릴 수 있음
        Member member = new Member();
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setGender(request.getGender());
        member.setHeight(request.getHeight());
        member.setWeight(request.getWeight());
        member.setPicture(request.getPicture());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
}
