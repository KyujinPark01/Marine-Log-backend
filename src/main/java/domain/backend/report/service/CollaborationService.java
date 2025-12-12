package domain.backend.report.service;

import domain.backend.organization.domain.entity.Organization;
import domain.backend.organization.repository.OrganizationRepository;
import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.entity.Collaboration;
import domain.backend.report.domain.enums.CollaborationStatus;
import domain.backend.report.domain.repository.ActivityReportRepository;
import domain.backend.report.domain.repository.CollaborationRepository;
import domain.backend.report.dto.response.CollaborationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollaborationService {

    private final CollaborationRepository collaborationRepository;
    private final OrganizationRepository organizationRepository;
    private final ActivityReportRepository reportRepository; // 추가됨

    // ✅ [수정] ActivityReport 객체 대신 ID를 받도록 변경
    @Transactional
    public void createRequest(Long reportId, Long targetOrgId) {

        // 1. Report 조회
        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        // 2. 요청하는 단체 (Partner) 조회
        Organization partnerOrg = organizationRepository.findById(targetOrgId)
                .orElseThrow(() -> new IllegalArgumentException("Partner Organization not found"));

        // 3. 제보의 주인 (Host) - 요청 받는 사람
        Organization hostOrg = report.getReporter().getOrganization();

        // 4. 중복 방지 (선택 사항)
        // if (collaborationRepository.existsBy...)

        Collaboration collab = Collaboration.builder()
                .activityReport(report)
                .hostOrg(partnerOrg)  // 요청한 곳 (상대방)
                .targetOrg(hostOrg)   // 요청 받은 곳 (우리)
                .status(CollaborationStatus.REQUESTED)
                .build();

        collaborationRepository.save(collab);
    }

    // 승인
    @Transactional
    public void approveRequest(Long reportId, Long partnerOrgId) {
        // ✅ [수정] 변경된 메서드 이름으로 호출
        Collaboration collab = collaborationRepository.findByReportAndPartner(reportId, partnerOrgId)
                .orElseThrow(() -> new IllegalArgumentException("연합 요청을 찾을 수 없습니다."));

        collab.approve();
    }

    // 반려
    @Transactional
    public void rejectRequest(Long reportId, Long partnerOrgId) {
        // ✅ [수정] 변경된 메서드 이름으로 호출
        Collaboration collab = collaborationRepository.findByReportAndPartner(reportId, partnerOrgId)
                .orElseThrow(() -> new IllegalArgumentException("연합 요청을 찾을 수 없습니다."));

        collab.reject();
    }

    // 목록 조회
    public List<CollaborationResponse> getByReport(Long reportId) {
        List<Collaboration> list = collaborationRepository.findByActivityReport_ActivityReportId(reportId);
        return list.stream()
                .map(CollaborationResponse::from)
                .toList();
    }
}