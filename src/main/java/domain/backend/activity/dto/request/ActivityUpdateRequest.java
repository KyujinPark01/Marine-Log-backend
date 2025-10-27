package domain.backend.activity.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)      //null 은 미변경
public class ActivityUpdateRequest {
    private String name;
    private String description;
    private LocalDate activityDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer participants;
    private Double weightKg;
    private Double volumeL;
    /*후순위*/
    //LocationDto location,
    //List<String> pictureUrls,            // 사진 url
    //List<HostedByDto> hostedBy           // 참여 단체
}
