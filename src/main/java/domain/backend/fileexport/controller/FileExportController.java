package domain.backend.fileexport.controller;

import domain.backend.common.dto.ApiResponse;
import domain.backend.common.security.auth.UserPrincipal;
import domain.backend.fileexport.service.ExportService;
import domain.backend.waste.domain.Waste;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileExportController {
    private final ExportService exportService;

    @PreAuthorize("hasRole('HOST')")
    @PostMapping("/v1/export/activities")
    public ResponseEntity<ApiResponse<?>> queueActivityExport(
            @RequestParam ActivityExportScope scope,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        exportService.queueActivityExport(scope.toString(), startDate, endDate, userPrincipal);

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PreAuthorize("hasRole('HOST')")
    @PostMapping("/v1/export/wastes")
    public ResponseEntity<ApiResponse<?>> queueWastesExport(
            @RequestParam WasteExportScope scope,
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        exportService.queueWasteExport(activityId, region, startDate, endDate, userPrincipal);

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    public enum ActivityExportScope {
        ORG, USER
    }

    public enum WasteExportScope {

    }
}
