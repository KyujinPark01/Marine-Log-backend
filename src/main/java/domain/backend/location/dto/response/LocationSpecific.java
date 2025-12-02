package domain.backend.location.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class LocationSpecific {
    private Long activityId;
    private String activityName;
    private LocalDate activityDate;
    private Double latitude;
    private Double longitude;
}
