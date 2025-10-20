package domain.backend.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LocationAggregationDto {
    private String region;
    private Integer counts;
    private double latitude;
    private double longitude;
}
