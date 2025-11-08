package domain.backend.dashboard.dto;

import domain.backend.dashboard.dto.activity.MaterialBreakdownDto;
import domain.backend.dashboard.dto.activity.MonthlyTrendDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RegionDashboardResponse {
    private Double totalWeightKg;
    private Integer wasteCount;
    private List<MaterialBreakdownDto> materials;
    private List<MonthlyTrendDto> trend;
}
