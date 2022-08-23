package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatisticsResponse {

    private StatisticsInfoVO dailyStatistics;
    private StatisticsInfoVO weeklyStatistics;
    private StatisticsInfoVO monthlyStatistics;
    private StatisticsInfoVO yearlyStatistics;
    private StatisticsInfoVO totalStatistics;


}
