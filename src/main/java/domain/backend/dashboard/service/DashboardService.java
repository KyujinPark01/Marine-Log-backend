package domain.backend.dashboard.service;

import domain.backend.dashboard.projection.MaterialShareRow;
import domain.backend.dashboard.projection.SumWasteView;
import domain.backend.dashboard.dto.*;
import domain.backend.dashboard.repository.ActivityDashRepository;
import domain.backend.dashboard.repository.WasteDashRepository;
import domain.backend.dashboard.repository.OrgDashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final ActivityDashRepository activityDashRepository;
    private final WasteDashRepository wasteDashRepository;
    private final OrgDashRepository orgDashRepository;

    public DashboardSummaryDto getSummary(DashboardFilter f) {
        Long activityCount = activityDashRepository.countActivities(f.getStart(), f.getEnd(), f.getOrgId());
        if (activityCount == null) activityCount = 0L;


        SumWasteView sum = wasteDashRepository.sumWaste(f.getStart(), f.getEnd(), f.getOrgId());
        Double totalKg = sum == null || sum.totalkg() == null ? 0.0 : sum.totalkg();
        Double totalL = sum == null || sum.totall() == null ? 0.0 : sum.totall();


        Double avg = activityCount == 0 ? 0.0 : totalKg / activityCount;


        return new DashboardSummaryDto(activityCount, totalKg, totalL, avg);
    }


    public List<MonthlySeriesDto> getTimeSeries(DashboardFilter f) {
        List<MonthlyAggRow> rows = activityDashRepository.monthlySeries(f.getStart(), f.getEnd(), f.getOrgId());
        return rows.stream()
                .map(r -> new MonthlySeriesDto(
                        String.format("%04d-%02d", r.year(), r.month()),
                        r.totalkg() == null ? 0.0 : r.totalkg(),
                        r.activitycount() == null ? 0L : r.activitycount()
                ))
                .toList();
    }


    public List<MaterialShareDto> getMaterialBreakdown(DashboardFilter f) {
        List<MaterialShareRow> rows = wasteDashRepository.materialBreakdown(f.getStart(), f.getEnd(), f.getOrgId());
        double total = rows.stream().mapToDouble(r -> r.kg() == null ? 0.0 : r.kg()).sum();
        return rows.stream()
                .map(r -> {
                    double kg = r.kg() == null ? 0.0 : r.kg();
                    double ratio = total == 0.0 ? 0.0 : kg / total;
                    return new MaterialShareDto(r.material(), kg, ratio);
                })
                .toList();
    }


    public List<OrgRankingDto> getOrgLeaderboard(DashboardFilter f, int limit) {
        return orgDashRepository.orgRanking(f.getStart(), f.getEnd(), f.getOrgId(), limit)
                .stream()
                .map(r -> new OrgRankingDto(r.orgid(), r.orgname(), r.totalkg() == null ? 0.0 : r.totalkg()))
                .toList();
    }
}