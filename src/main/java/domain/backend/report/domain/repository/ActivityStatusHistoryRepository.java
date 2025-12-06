package domain.backend.report.domain.repository;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.entity.ActivityStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityStatusHistoryRepository extends JpaRepository<ActivityStatusHistory, Long> {
}
