package domain.backend.report.controller;

import domain.backend.auth.domain.user.User;
import domain.backend.report.dto.ActivityReportCreateRequest;
import domain.backend.report.dto.ActivityReportResponse;
import domain.backend.report.service.ActivityReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/activity-report")
@RequiredArgsConstructor
public class ActivityReportController {

    private final ActivityReportService activityReportService;

    @PostMapping
    public ResponseEntity<Long> createActivityReport(@RequestPart("request") ActivityReportCreateRequest request,
                                                     @RequestPart("photo") MultipartFile photo) {
        Long reportId = activityReportService.createActivityReport(request, photo);
        return ResponseEntity.ok(reportId);
    }

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
