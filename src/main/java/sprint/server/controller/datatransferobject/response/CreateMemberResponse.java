package sprint.server.controller.datatransferobject.response;

import lombok.Data;

/**
 * 러닝 생성후 반환할 응답
 */
@Data
public class CreateMemberResponse {
    private Long id;

    public CreateMemberResponse(Long id) {
            this.id = id;
    }
}
