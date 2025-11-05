package domain.backend.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityReportCreateRequest {
    private Double latitude;
    private Double longitude;
    private String reportedReason;
    private String reportedOccupation;
}
