package domain.backend.dashboard.service;

import domain.backend.dashboard.dao.ActivityStatRepository;
import domain.backend.dashboard.dao.WasteStatRepository;
import domain.backend.dashboard.dto.response.StatSummaryResponse;
import domain.backend.dashboard.dto.response.MaterialBreakdownResponse;
import domain.backend.dashboard.dto.response.TimeSeriesPointResponse;
import domain.backend.dashboard.dto.request.StatFilterRequest;
import domain.backend.dashboard.projection.MaterialShareRow;
import domain.backend.dashboard.projection.MonthlyAggRow;
import domain.backend.dashboard.projection.SumWasteView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetWasteStatService {
    private final ActivityStatRepository activityDashRepository;
    private final WasteStatRepository wasteStatRepository;

    // 1. 총 폐기물 수거량
    public StatSummaryResponse getSummary(StatFilterRequest f) {
        Long activityCount = activityDashRepository.countActivities(f.getStart(), f.getEnd(), f.getOrgId());
        if (activityCount == null) activityCount = 0L;

        SumWasteView sum = wasteStatRepository.sumWaste(f.getStart(), f.getEnd(), f.getOrgId());
        Double totalKg = sum == null || sum.getTotalKg() == null ? 0.0 : sum.getTotalKg();
        Double totalL = sum == null || sum.getTotalL() == null ? 0.0 : sum.getTotalL();

        Double avg = activityCount == 0 ? 0.0 : totalKg / activityCount;

        return new StatSummaryResponse(activityCount, totalKg, totalL, avg);
    }

    // 2.월별 폐기물 수거량 추이
    public List<TimeSeriesPointResponse> getTimeSeries(StatFilterRequest f) {
        List<MonthlyAggRow> rows = activityDashRepository.monthlySeries(f.getStart(), f.getEnd(), f.getOrgId());
        return rows.stream()
                .map(r -> new TimeSeriesPointResponse(
                        String.format("%04d-%02d", r.getActivityYear(), r.getActivityMonth()),
                        r.getTotalKg() == null ? 0.0 : r.getTotalKg(),
                        r.getActivityCount() == null ? 0L : r.getActivityCount()
                ))
                .toList();
    }

    // 3. 폐기물 분류 파이 차트
    public List<MaterialBreakdownResponse> getMaterialBreakdown(StatFilterRequest f) {
        List<MaterialShareRow> rows = wasteStatRepository.materialBreakdown(f.getStart(), f.getEnd(), f.getOrgId());
        double total = rows.stream().mapToDouble(r -> r.getKg() == null ? 0.0 : r.getKg()).sum();
        return rows.stream()
                .map(r -> {
                    double kg = r.getKg() == null ? 0.0 : r.getKg();
                    double ratio = total == 0.0 ? 0.0 : kg / total;
                    return new MaterialBreakdownResponse(r.getMaterial(), kg, ratio);
                })
                .toList ();
    }

}
