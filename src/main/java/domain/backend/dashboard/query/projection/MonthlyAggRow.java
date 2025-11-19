package domain.backend.dashboard.query.projection;

public interface MonthlyAggRow {
    Integer getActivityYear();
    Integer getActivityMonth();
    Double getTotalKg();
    Long getActivityCount();
}
