package domain.backend.waste.dto.response;

import domain.backend.waste.domain.Waste;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WasteResponse {
    private Long wasteId;
    private String type;
    private String pictureUrl;
    private Double weightKg;
    private Double volumeL;

    public static WasteResponse from(Waste waste) {
        return WasteResponse.builder()
                .wasteId(waste.getWasteId())
                .type(waste.getType())
                .pictureUrl(waste.getPictureUrl())
                .weightKg(waste.getWeightKg())
                .volumeL(waste.getVolumeL())
                .build();
    }
}