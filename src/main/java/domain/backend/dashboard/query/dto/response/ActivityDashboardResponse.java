package domain.backend.dashboard.query.dto.response;

import domain.backend.dashboard.query.dto.MaterialBreakdownDto;
import domain.backend.dashboard.query.dto.MonthlyTrendDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ActivityDashboardResponse {
    private Long activityId;
    private String activityName;
    private Double totalWeightKg;
    private Integer wasteCount;
    private List<MaterialBreakdownDto> materials;
    private List<MonthlyTrendDto> trend;
}
