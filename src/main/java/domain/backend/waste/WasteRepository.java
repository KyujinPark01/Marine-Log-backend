package domain.backend.waste;

import domain.backend.common.entity.Waste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WasteRepository extends JpaRepository<Waste, Long> {
    List<Waste> findByActivity_ActivityId(Long activityId);
}
