package domain.backend.dashboard.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyTrendDto {
    private String month;
    private Double totalKg;
}
