package domain.backend.dashboard.query.dto;

public record OrgRankingDto(
        Long orgId,
        String orgName,
        Double totalKg
) { }
