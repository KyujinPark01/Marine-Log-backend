package domain.backend.dashboard.query.dto.response;

import domain.backend.dashboard.query.dto.MaterialBreakdownDto;
import domain.backend.dashboard.query.dto.TimeSeriesPointDto;

import java.util.List;

public record RegionDashboardResponse(
        Double totalWeightKg,
        Integer wasteCount,
        List<MaterialBreakdownDto> materials,
        List<TimeSeriesPointDto> trend
) { }
