package domain.backend.dashboard.service;

import domain.backend.dashboard.projection.SumWasteView;
import domain.backend.dashboard.repository.ActivityStatRepository;
import domain.backend.dashboard.repository.WasteStatRepository;
import domain.backend.dashboard.dto.response.StatSummaryResponse;
import domain.backend.dashboard.dto.response.MaterialBreakdownResponse;
import domain.backend.dashboard.dto.response.TimeSeriesPointResponse;
import domain.backend.dashboard.dto.request.StatFilterRequest;
import domain.backend.dashboard.projection.MaterialShareRow;
import domain.backend.dashboard.projection.MonthlyAggRow;
import domain.backend.dashboard.repository.testing.Case2TestRepository;
import domain.backend.dashboard.repository.testing.Case3TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetWasteStatService {

    private final ActivityStatRepository activityStatRepository;
    private final WasteStatRepository wasteStatRepository;

    // for testing
    private final Case2TestRepository case2TestRepository;
    private final Case3TestRepository case3TestRepository;

    // 1. 총 폐기물 수거량
    // activity id에 따라
    public StatSummaryResponse getSummary(StatFilterRequest request) {

        /*
        // case 1:
        // Using a single aggregated query
        // Using With queries, or CTE(Common Table Expressions)
        StatSummaryResponse statSummaryResponse =
                activityStatRepository.summarizedStat(request.getStart(), request.getEnd(), request.getOrgId());

        if (statSummaryResponse == null) {
            return new StatSummaryResponse(0L, 0.0, 0.0, 0.0);
        }

        long activityCount = statSummaryResponse.activityCount();
        Double totalKg = statSummaryResponse.totalKg() == null ? 0.0 : statSummaryResponse.totalKg();
        Double totalL = statSummaryResponse.totalL() == null ? 0.0 : statSummaryResponse.totalL();
        Double avgKg = statSummaryResponse.avgKgPerActivity() == null
                ? (activityCount == 0 ? 0.0 : totalKg / activityCount)
                : statSummaryResponse.avgKgPerActivity();

        return new StatSummaryResponse(activityCount, totalKg, totalL, avgKg);


        // case 2.
        // Using a single aggregated query
        // But not using With queries
        StatSummaryResponse statSummaryResponse = case2TestRepository.summarizedStat(request.getStart(), request.getEnd(), request.getOrgId());
        Long activityCount = statSummaryResponse.activityCount();
        Double totalKg = statSummaryResponse.totalKg() == null ? 0.0 : statSummaryResponse.totalKg();
        Double totalL = statSummaryResponse.totalL() == null ? 0.0 : statSummaryResponse.totalL();

        Double avg = activityCount == 0 ? 0.0 : totalKg / activityCount;

        return new StatSummaryResponse(activityCount, totalKg, totalL, avg);


        */

        // case 3.
        // Using two queries
        Long activityCount = case3TestRepository.countActivities(request.getStart(), request.getEnd(), request.getOrgId());
        if (activityCount == null) activityCount = 0L;

        SumWasteView sum = case3TestRepository.sumWaste(request.getStart(), request.getEnd(), request.getOrgId());
        Double totalKg = sum == null || sum.getTotalKg() == null ? 0.0 : sum.getTotalKg();
        Double totalL = sum == null || sum.getTotalL() == null ? 0.0 : sum.getTotalL();

        Double avg = activityCount == 0 ? 0.0 : totalKg / activityCount;
        return new StatSummaryResponse(activityCount, totalKg, totalL, avg);


    }

    // 2. 폐기물 분류 파이 차트
    // activity id에 따라
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

    // 3.월별 폐기물 수거량 추이
    // acitivity id에 따라
    public List<TimeSeriesPointResponse> getTimeSeries(StatFilterRequest f) {
        List<MonthlyAggRow> rows = activityStatRepository.monthlySeries(f.getStart(), f.getEnd(), f.getOrgId());
        return rows.stream()
                .map(r -> new TimeSeriesPointResponse(
                        String.format("%04d-%02d", r.getActivityYear(), r.getActivityMonth()),
                        r.getTotalKg() == null ? 0.0 : r.getTotalKg(),
                        r.getActivityCount() == null ? 0L : r.getActivityCount()
                ))
                .toList();
    }
}
