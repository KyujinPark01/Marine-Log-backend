package domain.backend.report.controller;

import domain.backend.auth.domain.user.Role;
import domain.backend.common.dto.ApiResponse;
import domain.backend.common.security.auth.UserPrincipal;
import domain.backend.report.dto.request.ActivityReportStatusUpdateRequest;
import domain.backend.report.dto.response.ActivityReportResponse;
import domain.backend.report.service.HandleActivityReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activity-report")
@RequiredArgsConstructor
@Validated
public class HostActivityReportController {

    private final HandleActivityReportService handleActivityReportService;

    @PreAuthorize("hasRole('HOST')")
    @PostMapping("/{activityReportId}/suggest-union")
    public ResponseEntity<ApiResponse<Void>> suggestUnionActivity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long activityReportId) {

        handleActivityReportService.suggestUnionActivity(activityReportId, principal.userId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PreAuthorize("hasRole('HOST')")
    @PatchMapping("/{activityReportId}")
    public ResponseEntity<ApiResponse<ActivityReportResponse>> acceptActivityReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long activityReportId,
            @RequestBody ActivityReportStatusUpdateRequest request) {

        ActivityReportResponse response =
                handleActivityReportService.updateStatus(activityReportId, request);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
