package domain.backend.report.controller;

import domain.backend.auth.domain.user.User;
import domain.backend.report.dto.request.ActivityReportCreateRequest;
import domain.backend.report.dto.response.ActivityReportResponse;
import domain.backend.report.dto.request.ActivityReportStatusUpdateRequest;
import domain.backend.report.service.HandleActivityReportService;
import domain.backend.report.service.ReadActivityReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/*
 * 단체장 권한을 위한 기능 구현
 * 아래와 같은 리스트를 처리:
 * 1. (단체장 권한을 확인 후) 타 단체의 연합 활동을 제안
 */

@RestController
@RequestMapping("/activity-report")
@RequiredArgsConstructor
public class ActivityReportController {

    private final HandleActivityReportService activityReportService;

    @PostMapping
    public ResponseEntity<Long> createActivityReport(@RequestPart("request") ActivityReportCreateRequest request,
                                                     @RequestPart("photo") MultipartFile photo) {
        Long reportId = activityReportService.createActivityReport(request, photo);
        return ResponseEntity.ok(reportId);
    }

}
