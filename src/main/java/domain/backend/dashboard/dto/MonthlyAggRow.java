package domain.backend.dashboard.dto;

public interface MonthlyAggRow {
    Integer year();
    Integer month();
    Double totalkg();
    Long activitycount();
}