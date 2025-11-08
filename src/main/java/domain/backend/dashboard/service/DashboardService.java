package domain.backend.dashboard.service;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.repository.ActivityRepository;
import domain.backend.common.entity.Waste;
import domain.backend.dashboard.dto.ActivityDashboardResponse;
import domain.backend.dashboard.dto.activity.MaterialBreakdownDto;
import domain.backend.dashboard.dto.activity.MonthlyTrendDto;
import domain.backend.dashboard.projection.MaterialShareRow;
import domain.backend.dashboard.projection.SumWasteView;
import domain.backend.dashboard.dto.*;
import domain.backend.dashboard.repository.ActivityDashRepository;
import domain.backend.dashboard.repository.RegionDashRepository;
import domain.backend.dashboard.repository.WasteDashRepository;
import domain.backend.dashboard.repository.OrgDashRepository;
import domain.backend.waste.WasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final ActivityDashRepository activityDashRepository;
    private final WasteDashRepository wasteDashRepository;
    private final OrgDashRepository orgDashRepository;
    private final ActivityRepository activityRepository;
    private final WasteRepository wasteRepository;
    private final RegionDashRepository regionDashRepository;
    public DashboardSummaryDto getSummary(DashboardFilter f) {
        Long activityCount = activityDashRepository.countActivities(f.getStart(), f.getEnd(), f.getOrgId());
        if (activityCount == null) activityCount = 0L;

        SumWasteView sum = wasteDashRepository.sumWaste(f.getStart(), f.getEnd(), f.getOrgId());
        Double totalKg = sum == null || sum.getTotalKg() == null ? 0.0 : sum.getTotalKg();
        Double totalL = sum == null || sum.getTotalL() == null ? 0.0 : sum.getTotalL();

        Double avg = activityCount == 0 ? 0.0 : totalKg / activityCount;

        return new DashboardSummaryDto(activityCount, totalKg, totalL, avg);
    }


    public List<MonthlySeriesDto> getTimeSeries(DashboardFilter f) {
        List<MonthlyAggRow> rows = activityDashRepository.monthlySeries(f.getStart(), f.getEnd(), f.getOrgId());
        return rows.stream()
                .map(r -> new MonthlySeriesDto(
                        String.format("%04d-%02d", r.getActivityYear(), r.getActivityMonth()),
                        r.getTotalKg() == null ? 0.0 : r.getTotalKg(),
                        r.getActivityCount() == null ? 0L : r.getActivityCount()
                ))
                .toList();
    }

    public List<MaterialShareDto> getMaterialBreakdown(DashboardFilter f) {
        List<MaterialShareRow> rows = wasteDashRepository.materialBreakdown(f.getStart(), f.getEnd(), f.getOrgId());
        double total = rows.stream().mapToDouble(r -> r.getKg() == null ? 0.0 : r.getKg()).sum();
        return rows.stream()
                .map(r -> {
                    double kg = r.getKg() == null ? 0.0 : r.getKg();
                    double ratio = total == 0.0 ? 0.0 : kg / total;
                    return new MaterialShareDto(r.getMaterial(), kg, ratio);
                })
                .toList ();
    }


    public List<OrgRankingDto> getOrgLeaderboard(DashboardFilter f, int limit) {
        return orgDashRepository.orgRanking(f.getStart(), f.getEnd(), f.getOrgId(), limit)
                .stream()
                .map(r -> new OrgRankingDto(r.getOrgId(), r.getOrgName(), r.getTotalKg() == null ? 0.0 : r.getTotalKg()))
                .toList();
    }
    public ActivityDashboardResponse getActivityDashboard(Long activityId) {
        // ✅ 활동 확인
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 활동입니다."));

        // ✅ 해당 활동에 속한 폐기물 전부 조회
        List<Waste> wastes = wasteRepository.findByActivity_ActivityId(activityId);

        double totalWeightKg = wastes.stream()
                .mapToDouble(w -> w.getWeightKg() != null ? w.getWeightKg() : 0)
                .sum();

        Map<String, Double> groupedByType = wastes.stream()
                .collect(Collectors.groupingBy(
                        w -> w.getType() != null ? w.getType() : "기타",
                        Collectors.summingDouble(w -> w.getWeightKg() != null ? w.getWeightKg() : 0)
                ));

        List<MaterialBreakdownDto> materials = groupedByType.entrySet().stream()
                .map(e -> new MaterialBreakdownDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        String month = activity.getActivityDate() != null
                ? activity.getActivityDate().getYear() + "-" + String.format("%02d", activity.getActivityDate().getMonthValue())
                : "unknown";

        List<MonthlyTrendDto> trend = List.of(new MonthlyTrendDto(month, totalWeightKg));

        return ActivityDashboardResponse.builder()
                .activityId(activityId)
                .activityName(activity.getName())
                .totalWeightKg(totalWeightKg)
                .wasteCount(wastes.size())
                .materials(materials)
                .trend(trend)
                .build();
    }

    public RegionDashboardResponse getRegionDashboard(RegionRequest request) {
        List<Long> activityIds = regionDashRepository.findActivityIdsInBounds(
                request.getSwLat(), request.getSwLng(), request.getNeLat(), request.getNeLng()
        );

        if (activityIds.isEmpty()) {
            return RegionDashboardResponse.builder()
                    .totalWeightKg(0.0)
                    .wasteCount(0)
                    .materials(List.of())
                    .trend(List.of())
                    .build();
        }

        RegionDashRepository.WasteSummaryInRegion summary = regionDashRepository.getSummaryInActivities(activityIds);
        List<RegionDashRepository.MaterialBreakdownInRegion> materialBreakdowns = regionDashRepository.getMaterialBreakdownInActivities(activityIds);
        List<RegionDashRepository.MonthlyTrendInRegion> monthlyTrends = regionDashRepository.getMonthlyTrendInActivities(activityIds);

        List<MaterialBreakdownDto> materials = materialBreakdowns.stream()
                .map(m -> new MaterialBreakdownDto(m.getMaterialType(), m.getTotalKg()))
                .collect(Collectors.toList());

        List<MonthlyTrendDto> trend = monthlyTrends.stream()
                .map(t -> new MonthlyTrendDto(t.getMonth(), t.getTotalKg()))
                .collect(Collectors.toList());

        return RegionDashboardResponse.builder()
                .totalWeightKg(summary.getTotalWeightKg())
                .wasteCount(summary.getWasteCount())
                .materials(materials)
                .trend(trend)
                .build();
    }
}
