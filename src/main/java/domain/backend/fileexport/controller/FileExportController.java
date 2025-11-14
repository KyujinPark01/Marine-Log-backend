package domain.backend.fileexport.controller;

import domain.backend.fileexport.request.ExportRequest;
import domain.backend.fileexport.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class FileExportController {
    private final ExportService exportService;

    @PostMapping
    public void queueDataExport(@RequestBody ExportRequest exportRequest) {
        exportService.queueDataExport(exportRequest);
    }
}
