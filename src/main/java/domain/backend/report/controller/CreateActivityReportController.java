package domain.backend.report.controller;

import domain.backend.report.dto.request.ActivityReportCreateRequest;
import domain.backend.report.service.HandleActivityReportService;
import domain.backend.report.service.ReadActivityReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(
        name = "Activity Report API",
        description = "활동 제보 등록 및 지도 표시용 제보 데이터 조회 API"
)
@RestController
@RequestMapping("/activity-report")
@RequiredArgsConstructor
public class CreateActivityReportController {

    private final HandleActivityReportService handleActivityReportService;
    private final ReadActivityReportService readActivityReportService;

    @Operation(summary = "활동 제보 등록", description = "활동 제보 데이터를 업로드하여 등록합니다.")
    @PostMapping
    public ResponseEntity<Long> createActivityReport(@RequestPart("request") ActivityReportCreateRequest request,
                                                     @RequestPart("photo") MultipartFile photo) {
        Long reportId = handleActivityReportService.createActivityReport(request, photo);
        return ResponseEntity.ok(reportId);
    }
}
