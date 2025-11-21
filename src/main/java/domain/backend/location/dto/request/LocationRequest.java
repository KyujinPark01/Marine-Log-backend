package domain.backend.location.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class LocationRequest {
    @DecimalMin(value = "-90.0", message = "swLat는 -90 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "swLat는 90 이하이어야 합니다.")
    private double swLat;

    @DecimalMin(value = "-180.0", message = "swLng는 -180 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "swLng는 180 이하이어야 합니다.")
    private double swLng;

    @DecimalMin(value = "-90.0", message = "neLat는 -90 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "neLat는 90 이하이어야 합니다.")
    private double neLat;

    @DecimalMin(value = "-180.0", message = "neLng는 -180 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "neLng는 180 이하이어야 합니다.")
    private double neLng;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Long orgId;
}
