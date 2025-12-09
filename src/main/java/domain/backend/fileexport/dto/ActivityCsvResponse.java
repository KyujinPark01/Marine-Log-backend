package domain.backend.fileexport.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public class ActivityCsvResponse {
    @CsvBindByName(column = "활동명")
    private String name;

    @CsvBindByName(column = "개요")
    private String description;

    @CsvBindByName(column = "날짜")
    private LocalDate activityDate;

    @CsvBindByName(column = "시작 시각")
    private LocalTime startTime;

    @CsvBindByName(column = "종료 시각")
    private LocalTime endTime;

    @CsvBindByName(column = "참여 인원수")
    private Integer participants;

    @CsvBindByName(column = "수거 폐기물량 (Kg)")
    private Double weightKg;

    @CsvBindByName(column = "수거 폐기물량 (L)")
    private Double volumeL;

    @CsvBindByName(column = "주소")
    private String address;
}
