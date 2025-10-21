package domain.backend.location.projection;

import java.time.LocalDate;

public interface LocationSpecificProjection {
    public Long getActivityId();
    public String getActivityName();
    public LocalDate getActivityDate();
    public Double getLatitude();
    public Double getLongitude();
}
