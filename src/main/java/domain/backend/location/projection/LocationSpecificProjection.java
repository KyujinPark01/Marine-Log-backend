package domain.backend.location.projection;

import java.time.LocalDateTime;

public interface LocationSpecificProjection {
    public Long getActivityId();
    public String getActivityName();
    public LocalDateTime getActivityDate();
    public Double getLatitude();
    public Double getLongitude();
}
