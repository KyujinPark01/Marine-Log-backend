package domain.backend.dashboard.query.dto;

public record MaterialBreakdownDto(
        String material,
        double kg,
        Double ratio
) { }
