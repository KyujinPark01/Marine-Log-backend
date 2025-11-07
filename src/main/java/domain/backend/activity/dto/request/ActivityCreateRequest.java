package domain.backend.activity.dto.request;

import domain.backend.organization.dto.HostedByDto;
import domain.backend.waste.dto.WasteRequest;
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
    private String name;
    private String description;
    private LocalDate activityDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer participants;
    private Double weightKg;
    private Double volumeL;
    /*후순위*/
//    List<String> pictureUrls;
    private Double lat;
    private Double lng;
    private List<HostedByDto> hostedByList;
    private List<WasteRequest> wastes;// 참여 단체
}