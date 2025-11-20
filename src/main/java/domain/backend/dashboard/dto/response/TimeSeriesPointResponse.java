package domain.backend.dashboard.dto.response;

public record TimeSeriesPointResponse(
        String month,
        double totalKg,
        Long activityCount
) { }
