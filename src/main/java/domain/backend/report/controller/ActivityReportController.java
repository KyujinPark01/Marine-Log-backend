package domain.backend.report.controller;

import domain.backend.report.dto.ActivityReportCreateRequest;
import domain.backend.report.dto.ActivityReportResponse;
import domain.backend.report.service.ActivityReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity-report")
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
}
