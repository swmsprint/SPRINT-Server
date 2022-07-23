package sprint.server.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sprint.server.domain.Member;
import sprint.server.service.MemberService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/api/members") //요청을 온걸 리퀘스트 바디로 받아,.
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        //api 스팩이 안바뀜. 누가 username으로 변환해버리면,
        // 그냥 setName 을 setUserName으로 바꿔버리면 되기에 전혀 신경쓸필요 ㄴㄴ
        //즉 엔티티와 api 스펙을 분리해버릴 수 있음
        Member member = new Member();
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        private long id;
        private String name;
        private String email;
        private float height;
        private float weight;
        private int mainGroupId;
        private int tierId;
        private String picture;
    }
}
