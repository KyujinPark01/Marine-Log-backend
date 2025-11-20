package domain.backend.dashboard.dto.response;

public record OrgRankingResponse(
        Long orgId,
        String orgName,
        Double totalKg
) { }
