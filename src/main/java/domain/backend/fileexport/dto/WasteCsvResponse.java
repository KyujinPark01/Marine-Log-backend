package domain.backend.fileexport.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;

@Builder
public class WasteCsvResponse {

    @CsvBindByName(column = "지역")
    String region;

    @CsvBindByName(column = "종류")
    String type;

    @CsvBindByName(column = "상세 종류")
    String specificType;

    @CsvBindByName(column = "수거량 (Kg)")
    Double weightKg;

    @CsvBindByName(column = "수거량 (L)")
    Double volumeL;
}
