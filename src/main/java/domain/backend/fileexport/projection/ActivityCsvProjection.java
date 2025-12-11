package domain.backend.fileexport.projection;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ActivityCsvProjection {
    String getName();
    String getDescription();
    LocalDate getActivityDate();
    LocalTime getStartTime();
    LocalTime getEndTime();
    Integer getParticipants();
    Double getWeightKg();
    Double getVolumeL();
    String getAddress();
}
