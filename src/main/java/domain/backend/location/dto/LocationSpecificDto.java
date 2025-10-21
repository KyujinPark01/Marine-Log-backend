package domain.backend.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LocationSpecificDto {
    private Long activityId;
    private String activityName;
    private LocalDate activityDate;
    private Double latitude;
    private Double longitude;
}
