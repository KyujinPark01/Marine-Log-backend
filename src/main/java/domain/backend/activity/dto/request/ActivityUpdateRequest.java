package domain.backend.activity.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import domain.backend.organization.dto.HostedByDto;
import domain.backend.waste.dto.WasteRequest;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)      //null 은 미변경
public class ActivityUpdateRequest {
    private String name;
    private String description;
    private LocalDate activityDate;
    private LocalTime startTime;
    private LocalTime endTime;

    @Positive(message = "참여 인원은 1명 이상이어야 합니다.")
    private Integer participants;

    @Positive(message = "수거 무게(kg)는 0보다 커야 합니다.")
    private Double weightKg;

    @Positive(message = "수거 부피(L)는 0보다 커야 합니다.")
    private Double volumeL;

    private Double lat;
    private Double lng;
    private List<HostedByDto> hostedByList; // 참여 단체
    private List<WasteRequest> wastes;
}
