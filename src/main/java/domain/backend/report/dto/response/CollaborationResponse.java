package domain.backend.report.dto.response;

import domain.backend.report.domain.entity.Collaboration;
import domain.backend.report.domain.enums.CollaborationStatus;
import lombok.Builder;

@Builder
public record CollaborationResponse(
        Long collaborationId,
        Long reportId,
        Long hostOrgId,
        String hostOrgName,
        Long targetOrgId,
        String targetOrgName,
        CollaborationStatus status
) {

    public static CollaborationResponse from(Collaboration collab) {
        return CollaborationResponse.builder()
                .collaborationId(collab.getId())
                .reportId(collab.getActivityReport().getActivityReportId())
                .hostOrgId(collab.getHostOrg().getOrgId())
                .hostOrgName(collab.getHostOrg().getName())
                .targetOrgId(collab.getTargetOrg().getOrgId())
                .targetOrgName(collab.getTargetOrg().getName())
                .status(collab.getStatus())
                .build();
    }
}
