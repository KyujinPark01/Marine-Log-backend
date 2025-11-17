package domain.backend.dashboard.query.dto;

public interface MonthlyAggRow {
    Integer getActivityYear();
    Integer getActivityMonth();
    Double getTotalKg();
    Long getActivityCount();
}