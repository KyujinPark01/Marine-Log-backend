package domain.backend.dashboard.dto;

public record MonthlySeriesDto(
        String year,
        Double totalKg,
        long activityCount
) { }
