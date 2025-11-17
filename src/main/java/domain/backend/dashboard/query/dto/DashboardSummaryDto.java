package domain.backend.dashboard.query.dto;

public record DashboardSummaryDto(
        long activityCount,
        Double totalKg,
        Double totalL,
        Double avgKgPerActivity
) { }
