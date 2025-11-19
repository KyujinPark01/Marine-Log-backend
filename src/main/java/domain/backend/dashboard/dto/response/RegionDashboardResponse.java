package domain.backend.dashboard.dto.response;

import domain.backend.dashboard.dto.MaterialBreakdownDto;
import domain.backend.dashboard.dto.TimeSeriesPointDto;

import java.util.List;

public record RegionDashboardResponse(
        Double totalWeightKg,
        Integer wasteCount,
        List<MaterialBreakdownDto> materials,
        List<TimeSeriesPointDto> trend
) { }
