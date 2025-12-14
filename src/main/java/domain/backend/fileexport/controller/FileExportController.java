package domain.backend.fileexport.controller;

import domain.backend.fileexport.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/v1/export/activities")
    public ResponseEntity<ByteArrayResource> exportActivities (
            @RequestParam(defaultValue = "1900-01-01")
            LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().toString()}")
            LocalDate endDate) {

        ByteArrayResource resource = exportService.ActivityExport(startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=activities.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/v1/export/wastes")
    public ResponseEntity<ByteArrayResource> exportWastes (
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "1900-01-01")
            LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().toString()}")
            LocalDate endDate) {

        ByteArrayResource resource = exportService.WasteExport(region, startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=activities.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}
