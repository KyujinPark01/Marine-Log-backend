package domain.backend.location.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LocationWeightedResponse {
    private double latitude;
    private double longitude;
    private double weight;
}
