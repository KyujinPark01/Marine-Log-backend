package domain.backend.report.domain.repository;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityReportRepository extends JpaRepository<ActivityReport, Long> {
    List<ActivityReport> findByStatusIn(List<ActivityStatus> statuses);
    Page<ActivityReport> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<ActivityReport> findByStatusOrderByCreatedAtDesc(ActivityStatus status, Pageable pageable);
}
