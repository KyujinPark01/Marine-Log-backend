package domain.backend.dashboard.query.dto.response;

import domain.backend.dashboard.query.dto.MaterialBreakdownDto;
import domain.backend.dashboard.query.dto.MonthlyTrendDto;
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
