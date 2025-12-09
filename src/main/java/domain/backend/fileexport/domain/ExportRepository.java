package domain.backend.fileexport.domain;

import domain.backend.activity.domain.Activity;
import domain.backend.fileexport.projection.ActivityCsvProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExportRepository extends JpaRepository<Activity, Integer> {

    @Query(value = """
        SELECT
            a.name AS name,
            a.description AS description,
            a.activity_date AS activityDate,
            a.start_time AS startTime,
            a.end_time AS endTime,
            a.participants AS participants,
            a.weight_kg AS weightKg,
            a.volume_L AS volumeL,
            l.address AS address
        FROM activity AS a
        JOIN location AS l
        ON a.activity_id = l.activity_id
        WHERE a.activity_date BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    List<ActivityCsvProjection> getActivityByUserCsv(LocalDate startDate,
                                                     LocalDate endDate);
}
