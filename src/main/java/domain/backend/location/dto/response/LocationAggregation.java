package domain.backend.location.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LocationAggregation {
    private String region;
    private Integer counts;
    private double latitude;
    private double longitude;
}
