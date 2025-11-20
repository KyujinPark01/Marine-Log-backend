package domain.backend.dashboard.dto.response;

public record DashboardSummaryResponse(
        long activityCount,
        Double totalKg,
        Double totalL,
        Double avgKgPerActivity
) { }
