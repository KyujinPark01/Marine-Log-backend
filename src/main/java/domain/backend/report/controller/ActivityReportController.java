package domain.backend.report.controller;

import domain.backend.auth.domain.user.User;
import domain.backend.report.dto.request.ActivityReportCreateRequest;
import domain.backend.report.dto.response.ActivityReportResponse;
import domain.backend.report.dto.request.ActivityReportStatusUpdateRequest;
import domain.backend.report.service.ActivityReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Tag(
        name = "Activity Report API",
        description = "활동 제보 등록 및 지도 표시용 제보 데이터 조회 API"
)
@RestController
@RequestMapping("/activity-report")
@RequiredArgsConstructor
public class ActivityReportController {

    private final ActivityReportService activityReportService;
    @Operation(summary = "활동 제보 등록", description = "활동 제보 데이터를 업로드하여 등록합니다.")
    @PostMapping
    public ResponseEntity<Long> createActivityReport(@RequestPart("request") ActivityReportCreateRequest request,
                                                     @RequestPart("photo") MultipartFile photo) {
        Long reportId = activityReportService.createActivityReport(request, photo);
        return ResponseEntity.ok(reportId);
    }
    @Operation(summary = "지도 표시용 제보 조회", description = "지도로 표시할 모든 활동 제보 데이터를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ActivityReportResponse>> getAllReportsForMap() {
        List<ActivityReportResponse> reports = activityReportService.getAllReportsForMap();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{activityReportId}")
    public ResponseEntity<ActivityReportResponse> getSingleReports(@PathVariable Long activityReportId) {
        ActivityReportResponse report = activityReportService.getSingleReport(activityReportId);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/{activityReportId}/suggest-union")
    public ResponseEntity<Void> suggestUnionActivity(@PathVariable Long activityReportId) {
        // ToDo: Get user from security context
        User me = getCurrentUser();
        activityReportService.suggestUnionActivity(activityReportId, me);
        return ResponseEntity.ok().build();
    }

    // ToDo: Later add code for checking authority
    @PatchMapping("/{activityReportId}/status")
    public ResponseEntity<ActivityReportResponse> updateStatus(@PathVariable Long activityReportId,
                                                               @RequestBody ActivityReportStatusUpdateRequest request) {
        return ResponseEntity.ok(activityReportService.updateStatus(activityReportId, request));
    }

    /*[테스트 전용] Security 연동 후 대체 */
    private User getCurrentUser() {
        return new User(
                2L, // Assuming user 2 is sending the suggestion
                "user2@example.com",
                "User 2",
                "Busan, Korea",
                "010-2490-6535",
                domain.backend.auth.domain.user.Role.USER,
                null,
                null,
                null
        );
    }
}
