package domain.backend.organization.dto.response;

import domain.backend.organization.domain.entity.HostedBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HostedByResponse {
    private Long orgId;
    private String orgName;
    private Double contribution;

    public static HostedByResponse from(HostedBy entity) {
        return HostedByResponse.builder()
                .orgId(entity.getOrganization().getOrgId())
                .orgName(entity.getOrganization().getName())
                .contribution(entity.getContribution())
                .build();
    }
}
