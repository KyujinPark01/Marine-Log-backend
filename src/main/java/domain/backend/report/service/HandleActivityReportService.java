package domain.backend.report.service;

import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import domain.backend.image.service.ImageUploadService;
import domain.backend.notification.service.NotificationService;
import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.entity.ActivityStatus;
import domain.backend.report.dto.request.ActivityReportCreateRequest;
import domain.backend.report.dto.response.ActivityReportResponse;
import domain.backend.report.dto.request.ActivityReportStatusUpdateRequest;
import domain.backend.report.domain.repository.ActivityReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class HandleActivityReportService {

    private final ActivityReportRepository activityReportRepository;
    private final ImageUploadService imageUploadService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Transactional
    public Long createActivityReport(Long userId, ActivityReportCreateRequest request, MultipartFile photo) {

        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        String photoUrl = "";
        try {
            photoUrl = imageUploadService.uploadImage(photo);
        } catch (IOException e) {
            System.err.println("An IOException occurred: " + e.getMessage());
        }

        ActivityReport report = ActivityReport.builder()
                .reporter(reporter)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .reportedReason(request.getReportedReason())
                .reporterOccupation(request.getReportedOccupation())
                .status(ActivityStatus.REPORTED)
                .photoUrl(photoUrl)
                .title(request.getReportedReason())
                .build();

        activityReportRepository.save(report);
        return report.getActivityReportId();
    }


    @Transactional
    public void suggestUnionActivity(Long activityReportId, Long userId) {

        ActivityReport report = activityReportRepository.findById(activityReportId)
                .orElseThrow(() -> new IllegalArgumentException("ActivityReport not found"));

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User target = report.getReporter();

        String message = sender.getName() + "님이 '"
                + report.getTitle() + "' 활동에 대한 연합 활동을 제안했습니다.";

        notificationService.send(target, sender, message, "UNION_SUGGESTION");
    }


    @Transactional
    public ActivityReportResponse updateStatus(Long activityReportId, ActivityReportStatusUpdateRequest request) {
        ActivityReport report = activityReportRepository.findById(activityReportId)
                .orElseThrow(() -> new IllegalArgumentException("ActivityReport not found with id: " + activityReportId));

        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Status must be provided");
        }

        report.setStatus(request.getStatus());
        return ActivityReportResponse.from(report);
    }
}
