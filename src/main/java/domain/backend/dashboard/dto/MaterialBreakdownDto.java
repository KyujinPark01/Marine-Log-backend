package domain.backend.dashboard.dto;

public record MaterialBreakdownDto(
        String material,
        double kg,
        Double ratio
) { }
