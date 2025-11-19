package domain.backend.dashboard.query.service;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.repository.ActivityRepository;
import domain.backend.dashboard.query.dto.request.RegionRequest;
import domain.backend.dashboard.query.dto.response.RegionDashboardResponse;
import domain.backend.dashboard.query.dto.*;
import domain.backend.waste.domain.Waste;
import domain.backend.dashboard.query.dto.response.ActivityDashboardResponse;
import domain.backend.dashboard.query.dao.ActivityDashRepository;
import domain.backend.dashboard.query.dao.RegionDashRepository;
import domain.backend.dashboard.query.dao.WasteDashRepository;
import domain.backend.dashboard.query.dao.OrgDashRepository;
import domain.backend.waste.repository.WasteRepository;
import domain.backend.dashboard.query.projection.MaterialShareRow;
import domain.backend.dashboard.query.projection.MonthlyAggRow;
import domain.backend.dashboard.query.projection.SumWasteView;
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


    public List<TimeSeriesPointDto> getTimeSeries(DashboardFilter f) {
        List<MonthlyAggRow> rows = activityDashRepository.monthlySeries(f.getStart(), f.getEnd(), f.getOrgId());
        return rows.stream()
                .map(r -> new TimeSeriesPointDto(
                        String.format("%04d-%02d", r.getActivityYear(), r.getActivityMonth()),
                        r.getTotalKg() == null ? 0.0 : r.getTotalKg(),
                        r.getActivityCount() == null ? 0L : r.getActivityCount()
                ))
                .toList();
    }

    public List<MaterialBreakdownDto> getMaterialBreakdown(DashboardFilter f) {
        List<MaterialShareRow> rows = wasteDashRepository.materialBreakdown(f.getStart(), f.getEnd(), f.getOrgId());
        double total = rows.stream().mapToDouble(r -> r.getKg() == null ? 0.0 : r.getKg()).sum();
        return rows.stream()
                .map(r -> {
                    double kg = r.getKg() == null ? 0.0 : r.getKg();
                    double ratio = total == 0.0 ? 0.0 : kg / total;
                    return new MaterialBreakdownDto(r.getMaterial(), kg, ratio);
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
        // 활동 확인
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 활동입니다."));

        // 해당 활동에 속한 폐기물 전부 조회
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
                .map(e -> new MaterialBreakdownDto(
                        e.getKey(),
                        e.getValue() == null ? 0.0 : e.getValue(),
                        null
                ))
                .collect(Collectors.toList());

        String month = activity.getActivityDate() != null
                ? activity.getActivityDate().getYear() + "-" + String.format("%02d", activity.getActivityDate().getMonthValue())
                : "unknown";

        List<TimeSeriesPointDto> trend = List.of(new TimeSeriesPointDto(month, totalWeightKg, null));

        return new ActivityDashboardResponse(
                activityId,
                activity.getName(),
                totalWeightKg,
                wastes.size(),
                materials,
                trend
        );
    }

    public RegionDashboardResponse getRegionDashboard(RegionRequest request) {
        List<Long> activityIds = regionDashRepository.findActivityIdsInBounds(
                request.getSwLat(), request.getSwLng(), request.getNeLat(), request.getNeLng()
        );

        if (activityIds.isEmpty()) {
            return new RegionDashboardResponse(0.0, 0, List.of(), List.of());
        }

        RegionDashRepository.WasteSummaryInRegion summary = regionDashRepository.getSummaryInActivities(activityIds);
        List<RegionDashRepository.MaterialBreakdownInRegion> materialBreakdowns = regionDashRepository.getMaterialBreakdownInActivities(activityIds);
        List<RegionDashRepository.MonthlyTrendInRegion> monthlyTrends = regionDashRepository.getMonthlyTrendInActivities(activityIds);

        List<MaterialBreakdownDto> materials = materialBreakdowns.stream()
                .map(m -> new MaterialBreakdownDto(
                        m.getMaterialType(),
                        m.getTotalKg() == null ? 0.0 : m.getTotalKg(),
                        null
                ))
                .collect(Collectors.toList());

        List<TimeSeriesPointDto> trend = monthlyTrends.stream()
                .map(t -> new TimeSeriesPointDto(
                        t.getMonth(),
                        t.getTotalKg() == null ? 0.0 : t.getTotalKg(),
                        null
                ))
                .collect(Collectors.toList());

        return new RegionDashboardResponse(
                summary.getTotalWeightKg(),
                summary.getWasteCount(),
                materials,
                trend
        );
    }
}
