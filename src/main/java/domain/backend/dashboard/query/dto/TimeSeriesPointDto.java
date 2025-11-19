package domain.backend.dashboard.query.dto;

public record TimeSeriesPointDto(
        String month,
        double totalKg,
        Long activityCount
) { }
