package domain.backend.location.projection;

public interface LocationWeightedProjection {
    public Double getLatitude();
    public Double getLongitude();
    public Integer getWeight();
}
