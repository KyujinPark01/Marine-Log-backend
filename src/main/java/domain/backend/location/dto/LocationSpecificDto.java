package domain.backend.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LocationSpecificDto {
    private Long activityId;
    private String activityName;
    private LocalDateTime activityDate;
    private Double latitude;
    private Double longitude;
}
