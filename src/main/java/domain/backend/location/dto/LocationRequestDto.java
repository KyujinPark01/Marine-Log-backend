package domain.backend.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LocationRequestDto {
    private double swLat;
    private double swLng;
    private double neLat;
    private double neLng;
}
