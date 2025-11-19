package domain.backend.dashboard.controller;

import domain.backend.dashboard.dto.*;
import domain.backend.dashboard.dto.response.ActivityDashboardResponse;
import domain.backend.dashboard.dto.response.RegionDashboardResponse;
import domain.backend.dashboard.dto.request.RegionRequest;
import domain.backend.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Dashboard API", description = "대시보드 통계 및 지역별 활동 데이터 조회 API")

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
class DashboardController {
    private final DashboardService dashboardService;

    @Operation(summary = "요약 통계", description = "전체 활동 요약 통계를 조회합니다.")
    @GetMapping("/summary")
    public DashboardSummaryDto summary(DashboardFilter filter) {
        return dashboardService.getSummary(filter);
    }

    @Operation(summary = "월별 시계열 통계", description = "월별 활동 추이를 시계열로 조회합니다.")
    @GetMapping("/timeseries")
    public List<TimeSeriesPointDto> timeseries(DashboardFilter filter) {
        return dashboardService.getTimeSeries(filter);
    }

    @Operation(summary = "재질별 비율", description = "수거된 폐기물의 재질별 비율 데이터를 조회합니다.")
    @GetMapping("/material-breakdown")
    public List<MaterialBreakdownDto> material(DashboardFilter filter) {
        return dashboardService.getMaterialBreakdown(filter);
    }
    @Operation(summary = "단체 리더보드", description = "활동량 기준 단체 순위를 조회합니다.")
    @GetMapping("/org-leaderboard")
    public List<OrgRankingDto> leaderboard(DashboardFilter filter,
                                           @RequestParam(defaultValue = "10") int limit) {
        return dashboardService.getOrgLeaderboard(filter, Math.max(1, limit));
    }
    @Operation(summary = "활동별 대시보드", description = "특정 활동 ID에 대한 상세 통계 데이터를 조회합니다.")
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<ActivityDashboardResponse> getActivityDashboard(
            @PathVariable Long activityId) {
        ActivityDashboardResponse response = dashboardService.getActivityDashboard(activityId);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "지역별 대시보드", description = "선택한 지역의 통계 데이터를 조회합니다.")
    @PostMapping("/region")
    public ResponseEntity<RegionDashboardResponse> getRegionDashboard(@RequestBody RegionRequest request) {
        RegionDashboardResponse response = dashboardService.getRegionDashboard(request);
        return ResponseEntity.ok(response);
    }
}
