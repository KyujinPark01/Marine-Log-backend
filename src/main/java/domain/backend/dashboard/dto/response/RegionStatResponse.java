package domain.backend.dashboard.dto.response;

import java.util.List;

public record RegionStatResponse(
        Double totalWeightKg,
        Integer wasteCount,
        List<MaterialBreakdownResponse> materials,
        List<TimeSeriesPointResponse> trend
) { }
