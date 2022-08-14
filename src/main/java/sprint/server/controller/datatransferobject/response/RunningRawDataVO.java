package sprint.server.controller.datatransferobject.response;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RunningRawDataVO {
    private double latitude;
    private double longitude;
    private double speed;
    private String timestamp;

    public RunningRawDataVO(double latitude, double longitude,  double speed, String timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.timestamp = timestamp;
    }
}