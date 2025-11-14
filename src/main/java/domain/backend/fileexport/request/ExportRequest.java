package domain.backend.fileexport.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ExportRequest {
    private ExportType exportType;
    private Long activityId;
    private Long orgId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String region1depthName;

    public enum ExportType {
        ACTIVITY, WASTE
    }
}

