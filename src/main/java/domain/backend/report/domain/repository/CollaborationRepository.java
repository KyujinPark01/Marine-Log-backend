package domain.backend.report.domain.repository;

import domain.backend.report.domain.entity.Collaboration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CollaborationRepository extends JpaRepository<Collaboration, Long> {
    List<Collaboration> findByActivityReport_ActivityReportId(Long reportId);
    // ✅ [수정] JPQL을 사용하여 정확한 필드명(orgId) 매핑
    @Query("SELECT c FROM Collaboration c " +
            "WHERE c.activityReport.activityReportId = :reportId " +
            "AND c.hostOrg.orgId = :partnerOrgId")
    Optional<Collaboration> findByReportAndPartner(@Param("reportId") Long reportId,
                                                   @Param("partnerOrgId") Long partnerOrgId);
}


