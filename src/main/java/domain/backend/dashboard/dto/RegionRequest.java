package domain.backend.dashboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegionRequest {
    private Double swLat;
    private Double swLng;
    private Double neLat;
    private Double neLng;
}
