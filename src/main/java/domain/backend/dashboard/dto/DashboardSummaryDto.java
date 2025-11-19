package domain.backend.dashboard.dto;

public record DashboardSummaryDto(
        long activityCount,
        Double totalKg,
        Double totalL,
        Double avgKgPerActivity
) { }
