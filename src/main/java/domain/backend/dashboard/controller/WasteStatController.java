package domain.backend.dashboard.controller;

import domain.backend.common.dto.ApiResponse;
import domain.backend.dashboard.dto.response.StatSummaryResponse;
import domain.backend.dashboard.dto.response.MaterialBreakdownResponse;
import domain.backend.dashboard.dto.response.RegionStatResponse;
import domain.backend.dashboard.dto.request.RegionRequest;
import domain.backend.dashboard.dto.response.TimeSeriesPointResponse;
import domain.backend.dashboard.dto.request.StatFilterRequest;
import domain.backend.dashboard.service.GetWasteStatService;
import domain.backend.dashboard.service.GetStatFromSelectedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Dashboard API", description = "대시보드 통계 및 지역별 활동 데이터 조회 API")

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Validated
public class WasteStatController {


    private final GetWasteStatService getWasteStatService;
    private final GetStatFromSelectedService getStatFromSelectedService;

    // 1. 기간 필터, 단체 필터 적용시 동작, 지역 상관없이 전체 통계 조회
    @Operation(summary = "요약 통계", description = "전체 활동 요약 통계를 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<StatSummaryResponse>> summary(@Valid @ModelAttribute StatFilterRequest filter) {
        StatSummaryResponse response = getWasteStatService.getSummary(filter);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "월별 시계열 통계", description = "월별 활동 추이를 시계열로 조회합니다.")
    @GetMapping("/timeseries")
    public ResponseEntity<ApiResponse<List<TimeSeriesPointResponse>>> timeseries(@Valid @ModelAttribute StatFilterRequest filter) {
        List<TimeSeriesPointResponse> series = getWasteStatService.getTimeSeries(filter);
        return ResponseEntity.ok(ApiResponse.ok(series));
    }

    @Operation(summary = "재질별 비율", description = "수거된 폐기물의 재질별 비율 데이터를 조회합니다.")
    @GetMapping("/material-breakdown")
    public ResponseEntity<ApiResponse<List<MaterialBreakdownResponse>>> material(@Valid @ModelAttribute StatFilterRequest filter) {
        List<MaterialBreakdownResponse> breakdown = getWasteStatService.getMaterialBreakdown(filter);
        return ResponseEntity.ok(ApiResponse.ok(breakdown));
    }
    //====================================================================================

    // 2. 마우스로 드래그한 지역에 대한 상세 통계 조회
    @Operation(summary = "지역별 대시보드", description = "선택한 지역의 통계 데이터를 조회합니다.")
    @PostMapping("/region")
    public ResponseEntity<ApiResponse<RegionStatResponse>> getRegionDashboard(@Valid @RequestBody RegionRequest request) {
        RegionStatResponse response = getStatFromSelectedService.getStat(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    //====================================================================================

}
