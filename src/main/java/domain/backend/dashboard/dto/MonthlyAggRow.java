package domain.backend.dashboard.dto;

public interface MonthlyAggRow {
    Integer getActivityYear();
    Integer getActivityMonth();
    Double getTotalKg();
    Long getActivityCount();
}