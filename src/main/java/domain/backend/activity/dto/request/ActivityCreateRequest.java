package domain.backend.activity.dto.request;

import domain.backend.organization.dto.HostedByDto;
import domain.backend.waste.dto.WasteRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ActivityCreateRequest {
    @NotBlank(message = "활동명은 필수입니다.")
    private String name;

    private String description;

    @NotNull(message = "활동 날짜는 필수입니다.")
    private LocalDate activityDate;

    private LocalTime startTime;
    private LocalTime endTime;

    @Positive(message = "참여 인원은 1명 이상이어야 합니다.")
    private Integer participants;

    @Positive(message = "수거 무게(kg)는 0보다 커야 합니다.")
    private Double weightKg;

    @Positive(message = "수거 부피(L)는 0보다 커야 합니다.")
    private Double volumeL;

    /*후순위*/
//    List<String> pictureUrls;

    private Double lat;
    private Double lng;
    private List<HostedByDto> hostedByList; // 참여 단체
    private List<WasteRequest> wastes;
}