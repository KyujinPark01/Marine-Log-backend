package domain.backend.dashboard.dto;

public record MonthlySeriesDto(
        String ym,
        Double totalKg,
        long activityCount
) { }
