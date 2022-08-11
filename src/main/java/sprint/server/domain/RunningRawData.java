package sprint.server.domain;


import lombok.Getter;

@Getter
public class RunningRawData {
    private double latitude;
    private double longitude;
    private double elevation;
    private double speed;
    private String timestamp;
    protected RunningRawData(){
        //함부로 생성하는 걸 막는다는 의미
    }
}