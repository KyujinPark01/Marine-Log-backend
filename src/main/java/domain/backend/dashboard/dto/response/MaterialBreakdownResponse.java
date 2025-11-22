package domain.backend.dashboard.dto.response;

public record MaterialBreakdownResponse(
        String material,
        double kg,
        Double ratio
) { }
