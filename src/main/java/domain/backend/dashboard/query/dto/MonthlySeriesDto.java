package domain.backend.dashboard.query.dto;

public record MonthlySeriesDto(
        String year,
        Double totalKg,
        long activityCount
) { }
