package domain.backend.dashboard.query.dto;

public record MaterialShareDto(
        String material,
        Double kg,
        Double ratio
) { }
