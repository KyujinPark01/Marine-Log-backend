package domain.backend.dashboard.projection;

public interface MonthlyAggRow {
    Integer getActivityYear();
    Integer getActivityMonth();
    Double getTotalKg();
    Long getActivityCount();
}
