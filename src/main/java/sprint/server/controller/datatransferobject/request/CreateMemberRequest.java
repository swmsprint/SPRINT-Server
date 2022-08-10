package sprint.server.controller.datatransferobject.request;

import lombok.Data;

@Data
public class CreateMemberRequest {
    private String name;
    private String email;
    private float height;
    private float weight;
    private String picture;
}
