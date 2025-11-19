package domain.backend.dashboard.dto;

public record TimeSeriesPointDto(
        String month,
        double totalKg,
        Long activityCount
) { }
