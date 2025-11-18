package domain.backend.report.domain.repository;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.entity.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityReportRepository extends JpaRepository<ActivityReport, Long> {
    List<ActivityReport> findByStatusIn(List<ActivityStatus> statuses);
}
