package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import sprint.server.controller.datatransferobject.StatisticsDTO;

@Data
@AllArgsConstructor
public class ViewStatisticsResponse {

    private StatisticsDTO dailyStatistics;
    private StatisticsDTO weeklyStatistics;
    private StatisticsDTO monthlyStatistics;
    private StatisticsDTO yearlyStatistics;
    private StatisticsDTO totalStatistics;


}
