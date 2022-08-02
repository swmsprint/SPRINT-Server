package sprint.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Member;
import sprint.server.domain.Running;
import sprint.server.domain.RunningRowData;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.RunningRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RunningService {

    private final MemberRepository memberRepository;
    private final RunningRepository runningRepository;


    public Optional<Running> findOne(Long runningId){
        return runningRepository.findById(runningId);
    }
    @Transactional
    public Long addRun(Long memberId){
        Member member = memberRepository.findById(memberId).get();

        Running running = Running.createRunning(member);
        running.setStartTime(new Timestamp(System.currentTimeMillis()));
        runningRepository.save(running);

        return running.getId();

    }


    @Transactional
    public void finishRunning(long runningId, long memberId, int duration, List<RunningRowData> rowData) throws JsonProcessingException {

        Running running = runningRepository.findById(runningId).get();
        Member member = memberRepository.findById(memberId).get();

        double distance = calculateTotalDistance(rowData);
        float weight = member.getWeight();
        double energy = calculateEnergy(weight, duration, distance);
        ObjectMapper mapper = new ObjectMapper();

        running.setEnergy(energy);
        running.setWeight(weight);
        running.setDuration(duration);
        running.setDistance(distance);
        running.setRowData(mapper.writeValueAsString(rowData));
    }

    /**
     *
     * @param rowData 경도, 위도, 고도, 시간 등의 데이터가 저장되어있음
     * @return
     */
    private double calculateTotalDistance(List<RunningRowData> rowData) {
        double distance = 0;
        for(int i = 0; i< rowData.size()-1; i++){
            distance += calculateDistance(rowData.get(i).getLongitude(),
                    rowData.get(i + 1).getLongitude(),
                    rowData.get(i).getLatitude(),
                    rowData.get(i + 1).getLatitude());
        }
        return distance;
    }

    /**
     *
     * @param weight 몸무게
     * @param duration 총 달린시간(초단위)
     * @param distance 총 달린거리(미터 단위)
     * @return 소모 칼로리 반환
     */
    public static double calculateEnergy(double weight, int duration, double distance){
        double coefficient;
        double speed =  distance /( 1000 * secondToHour(duration));
        /**칼로리 계산 매커니즘**/
        if(speed < 5){
            coefficient = 0.9;
        }else if(speed < 6.4){
            coefficient = 1.2;
        }else{
            coefficient = 2;
        }

        return coefficient * weight * secondToHour(duration)*6; //10분단위로 곱해진다 예를들어 0.2 시간 = 12분 = 1.2가 곱해져야함
    }

    /**
     *
     * @param duration 달린 시간 (초단위)
     * @return 초단위의 달린시간을 시간단위로 반환
     */
    private static double secondToHour(int duration){
        return (duration/3600.0);
    }

    /**
     *
     * @param longitudeX 출발지 경도
     * @param longitudeY 도착지 경도
     * @param latitudeX 출발지 위도
     * @param latitudeY 도착지 위도
     * @return 전체 뛴 거리 (m단위)
     */
    public static double calculateDistance(double longitudeX, double longitudeY, double latitudeX, double latitudeY){
        double theta = longitudeX - longitudeY;
        double distance = Math.sin(deg2rad(latitudeX))* Math.sin(deg2rad(latitudeY)) + Math.cos(deg2rad(latitudeX))*Math.cos(deg2rad(latitudeY))*Math.cos(deg2rad(theta));
        distance = Math.acos(distance);
        distance = rad2deg(distance);
        distance = distance * 60*1.1515*1609.344;

        return distance; //단위 meter
    }

    /**
     *
     * @param degree 일반 각도 입력
     * @return 각도를 라디안으로 변환
     */
    private static double deg2rad(double degree){
        return (degree * Math.PI/180.0);
    }

    /**
     *
     * @param radian 라디안 입력
     * @return 라디안을 각도로 반환
환    */
    private static double rad2deg(double radian){
        return (radian * 180 / Math.PI);
    }

}
