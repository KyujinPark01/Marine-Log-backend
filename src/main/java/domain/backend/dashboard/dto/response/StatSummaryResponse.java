package domain.backend.dashboard.dto.response;

public record StatSummaryResponse(
        long activityCount,
        Double totalKg,
        Double totalL,
        Double avgKgPerActivity
) { }
