package sprint.server.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@RequiredArgsConstructor
public class RunningRawData {

    @Id @GeneratedValue
    @Column(name = "raw_data_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_id")
    private Running running;

    private double latitude;
    private double longitude;
    private double speed;
    private String timestamp;

    public RunningRawData(Running running, double latitude, double longitude, double speed, String timestamp) {
        this.running = running;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.timestamp = timestamp;
    }
}