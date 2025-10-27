package domain.backend.dashboard.dto;

public record MaterialShareDto(
        String material,
        Double kg,
        Double ratio
) { }
