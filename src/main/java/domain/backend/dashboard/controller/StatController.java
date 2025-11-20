package domain.backend.dashboard.controller;

import domain.backend.dashboard.dto.response.DashboardSummaryResponse;
import domain.backend.dashboard.dto.response.MaterialBreakdownResponse;
import domain.backend.dashboard.dto.response.RegionDashboardResponse;
import domain.backend.dashboard.dto.request.RegionRequest;
import domain.backend.dashboard.dto.response.TimeSeriesPointResponse;
import domain.backend.dashboard.dto.request.DashboardFilter;
import domain.backend.dashboard.service.GetStatService;
import domain.backend.dashboard.service.GetStatFromSelectedService;
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
public class StatController {

    // 1. 기간 필터, 단체 필터 적용시 동작
    private final GetStatService getStatService;
    @Operation(summary = "요약 통계", description = "전체 활동 요약 통계를 조회합니다.")
    @GetMapping("/summary")
    public DashboardSummaryResponse summary(DashboardFilter filter) {
        return getStatService.getSummary(filter);
    }

    @Operation(summary = "월별 시계열 통계", description = "월별 활동 추이를 시계열로 조회합니다.")
    @GetMapping("/timeseries")
    public List<TimeSeriesPointResponse> timeseries(DashboardFilter filter) {
        return getStatService.getTimeSeries(filter);
    }

    @Operation(summary = "재질별 비율", description = "수거된 폐기물의 재질별 비율 데이터를 조회합니다.")
    @GetMapping("/material-breakdown")
    public List<MaterialBreakdownResponse> material(DashboardFilter filter) {
        return getStatService.getMaterialBreakdown(filter);
    }
    //====================================================================================

    // 2. 마우스로 드래그한 지역에 대한 상세 통계 필터
    private final GetStatFromSelectedService getStatFromSelectedService;
    @Operation(summary = "지역별 대시보드", description = "선택한 지역의 통계 데이터를 조회합니다.")
    @PostMapping("/region")
    public ResponseEntity<RegionDashboardResponse> getRegionDashboard(@RequestBody RegionRequest request) {
        RegionDashboardResponse response = getStatFromSelectedService.getStat(request);
        return ResponseEntity.ok(response);
    }

    //====================================================================================

}
