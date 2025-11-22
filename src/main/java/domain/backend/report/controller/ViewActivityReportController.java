package domain.backend.report.controller;

import domain.backend.common.dto.ApiResponse;
import domain.backend.report.dto.response.ActivityReportResponse;
import domain.backend.report.service.ReadActivityReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * 활동지도에서 활동 제보 핀들을 보기 위한 기능 구현
 * 아래와 같은 리스트를 처리:
 * 1. getAllReportsForMap(): 활동 제보 리스트를 모두 활동 지도에 출력
 * 2. getSingleReports(): 활동제보 1건의 세부 사항을 확인
 */

@RestController
@RequestMapping("/activity-report")
@RequiredArgsConstructor
@Validated
public class ViewActivityReportController {

    private final ReadActivityReportService readActivityReportService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityReportResponse>>> getAllReportsForMap() {
        List<ActivityReportResponse> reports = readActivityReportService.getAllReportsForMap();
        return ResponseEntity.ok(ApiResponse.ok(reports));
    }

    @GetMapping("/{activityReportId}")
    public ResponseEntity<ApiResponse<ActivityReportResponse>> getSingleReports(@PathVariable Long activityReportId) {
        ActivityReportResponse report = readActivityReportService.getSingleReport(activityReportId);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }
}
