package domain.backend.report.domain.repository;

import domain.backend.report.domain.entity.Collaboration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollaborationRepository extends JpaRepository<Collaboration, Long> {
    List<Collaboration> findByActivityReport_ActivityReportId(Long reportId);

    Optional<Collaboration> findByActivityReport_ActivityReportIdAndTargetOrg_OrgId(
            Long reportId,
            Long orgId
    );
}

