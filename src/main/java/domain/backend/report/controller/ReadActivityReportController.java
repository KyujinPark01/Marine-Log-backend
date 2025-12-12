package domain.backend.report.controller;

import domain.backend.common.dto.ApiResponse;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.dto.response.ActivityReportDetailResponse;
import domain.backend.report.dto.response.ActivityReportListResponse;
import domain.backend.report.service.ReadActivityReportListService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activity-report")
@RequiredArgsConstructor
@Validated
public class ReadActivityReportController {
    private final ReadActivityReportListService readActivityReportService;
//    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')") // 관리자 페이지 조회 권한 설정
    @Operation(summary = "활동 제보 목록 조회 (관리 페이지용)", description = "페이징 처리된 제보 목록을 최신순으로 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<ActivityReportListResponse>>> getReportsForAdmin(
            @PageableDefault(size = 5, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
            // ✅ status 파라미터 추가 (필수 아님)
            @RequestParam(required = false) ActivityStatus status
    ) {

        // Service에 status 전달
        Page<ActivityReportListResponse> reports = readActivityReportService.getPagedActivityReports(status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(reports));
    }

    @Operation(summary = "활동 제보 상세 조회", description = "특정 ID의 제보 상세 정보를 조회합니다.")
    @GetMapping("/list/{id}")
    public ResponseEntity<ApiResponse<ActivityReportDetailResponse>> getReportDetail(
            @PathVariable("id") Long reportId) {

        ActivityReportDetailResponse detail = readActivityReportService.getActivityReportDetail(reportId);
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }
}
