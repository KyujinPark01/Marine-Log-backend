package domain.backend.location.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LocationWeightedResponse {
    private double latitude;
    private double longitude;
    private double weight;
}
