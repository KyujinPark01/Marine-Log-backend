package domain.backend.report.service;

import domain.backend.organization.domain.entity.Organization;
import domain.backend.organization.repository.OrganizationRepository;
import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.entity.Collaboration;
import domain.backend.report.domain.enums.CollaborationStatus;
import domain.backend.report.domain.repository.CollaborationRepository;
import domain.backend.report.dto.response.CollaborationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaborationService {

    private final CollaborationRepository collaborationRepository;
    private final OrganizationRepository organizationRepository;

    // 연합 요청 생성
    public void createRequest(ActivityReport report, Long targetOrgId) {

        Organization hostOrg = report.getReporter().getOrganization(); // 요청하는 단체(HOST)
        Organization targetOrg = organizationRepository.findById(targetOrgId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        Collaboration collab = Collaboration.builder()
                .activityReport(report)
                .hostOrg(hostOrg)
                .targetOrg(targetOrg)
                .status(CollaborationStatus.REQUESTED)
                .build();

        collaborationRepository.save(collab);
    }

     //연합 요청 승인
    public void approve(Long collaborationId) {

        Collaboration collab = collaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new IllegalArgumentException("Collaboration not found"));

        collab.updateStatus(CollaborationStatus.APPROVED);
    }


     //연합 요청 거절
    public void reject(Long collaborationId) {

        Collaboration collab = collaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new IllegalArgumentException("Collaboration not found"));

        collab.updateStatus(CollaborationStatus.REJECTED);
    }
    //제보의 연합 신청 목록 조회
    public List<CollaborationResponse> getByReport(Long reportId) {

        List<Collaboration> list =
                collaborationRepository.findByActivityReport_ActivityReportId(reportId);

        return list.stream()
                .map(CollaborationResponse::from)
                .toList();
    }
}
