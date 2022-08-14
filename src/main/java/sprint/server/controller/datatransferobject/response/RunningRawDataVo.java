package sprint.server.controller.datatransferobject.response;


import lombok.Getter;

@Getter
public class RunningRawDataVo {
    private double latitude;
    private double longitude;
    private double elevation;
    private double speed;
    private String timestamp;
    protected RunningRawDataVo(){
        //함부로 생성하는 걸 막는다는 의미
    }
}