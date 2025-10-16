package domain.backend.location.projection;

public interface LocationAggregationProjection {
    public String getRegion();
    public Integer getCounts();
    public Double getLatitude();
    public Double getLongitude();

}
