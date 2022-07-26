package sprint.server.domain;


import lombok.Getter;

import javax.persistence.Embeddable;

@Getter
public class RunningRowData {
    private double latitude;
    private double longitude;
    private double elevation;
    private int time;

    protected RunningRowData(){
        //함부로 생성하는 걸 막는다는 의미
    }

    @Override
    public String toString(){
        return "latitude="+ latitude + "longitude="+longitude + "elevation="+elevation + "time="+time;
    }


}