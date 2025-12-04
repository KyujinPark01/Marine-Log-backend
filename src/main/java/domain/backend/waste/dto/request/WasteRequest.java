package domain.backend.waste.dto.request;

import lombok.Getter;

@Getter
public class WasteRequest {
    private String type;
//    private String specificType;
    private String pictureUrl;
    private Double weightKg;
    private Double volumeL;
}
