package domain.backend.location.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LocationAggregation {
    private String region;
    private Integer counts;
    private double latitude;
    private double longitude;
}
