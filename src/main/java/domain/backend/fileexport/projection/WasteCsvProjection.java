package domain.backend.fileexport.projection;

public interface WasteCsvProjection {
    String getSpecificType();
    String getType();
    Double getWeightKg();
    Double getVolumeL();
    String getRegion();
}
