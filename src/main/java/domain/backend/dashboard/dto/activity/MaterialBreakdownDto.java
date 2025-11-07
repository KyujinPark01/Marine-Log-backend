package domain.backend.dashboard.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MaterialBreakdownDto {
    private String materialType;
    private Double totalKg;
}
