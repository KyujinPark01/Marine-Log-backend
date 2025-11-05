package domain.backend.report.service;

import domain.backend.common.image.service.ImageUploadService;
import domain.backend.report.domain.ActivityReport;
import domain.backend.report.domain.ActivityStatus;
import domain.backend.report.dto.ActivityReportCreateRequest;
import domain.backend.report.dto.ActivityReportResponse;
import domain.backend.report.repository.ActivityReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityReportService {

    private final ActivityReportRepository activityReportRepository;
    private final ImageUploadService imageUploadService;

    @Transactional
    public Long createActivityReport(ActivityReportCreateRequest request, MultipartFile photo) {
        try {
            String photoUrl = imageUploadService.uploadImage(photo);
        } catch (IOException e){
            System.err.println("An IOException occurred: " + e.getMessage());
        }

        ActivityReport report = ActivityReport.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .reportedReason(request.getReportedReason())
                .reporterOccupation(request.getReportedOccupation())
                .status(ActivityStatus.REPORTED)
                //.photoUrl(photoUrl)
                .title(request.getReportedReason()) // Or create a more sophisticated title
                .build();

        activityReportRepository.save(report);

        return report.getId();
    }

    @Transactional(readOnly = true)
    public List<ActivityReportResponse> getAllReportsForMap() {
        List<ActivityStatus> statuses = List.of(ActivityStatus.APPROVED, ActivityStatus.SURVEYING, ActivityStatus.RECRUITING);
        return activityReportRepository.findByStatusIn(statuses).stream()
                .map(ActivityReportResponse::from)
                .collect(Collectors.toList());
    }
}
