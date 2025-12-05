package domain.backend.report.dto.request;

import domain.backend.report.domain.enums.ActivityStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivityReportStatusUpdateRequest {
    private ActivityStatus status;
}
