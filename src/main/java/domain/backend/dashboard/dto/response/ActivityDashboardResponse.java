package domain.backend.dashboard.dto.response;

import java.util.List;

public record ActivityDashboardResponse(
        Long activityId,
        String activityName,
        Double totalWeightKg,
        Integer wasteCount,
        List<MaterialBreakdownResponse> materials,
        List<TimeSeriesPointResponse> trend
) { }
