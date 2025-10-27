package domain.backend.dashboard.dto;

public record OrgRankingDto(
        Long orgId,
        String orgName,
        Double totalKg
) { }
