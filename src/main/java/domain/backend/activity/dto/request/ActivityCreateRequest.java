package domain.backend.activity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

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
        //LocationDto location,
        //List<String> pictureUrls,            // 사진 url
        //List<HostedByDto> hostedBy           // 참여 단체
}