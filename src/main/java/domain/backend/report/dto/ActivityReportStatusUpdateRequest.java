package domain.backend.report.dto;

import domain.backend.report.domain.ActivityStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivityReportStatusUpdateRequest {
    private ActivityStatus status;
}
