package domain.backend.dashboard.repository;


import domain.backend.dashboard.dto.MonthlyAggRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import domain.backend.activity.domain.Activity;

import java.time.LocalDate;
import java.util.List;

public interface ActivityDashRepository extends Repository<Activity, Long> {
    @Query(value = """
        SELECT COUNT(DISTINCT a.activity_id)
        FROM activity a
        LEFT JOIN hosted_by hb ON hb.activity_id = a.activity_id
        WHERE (:start IS NULL OR a.activity_date >= :start)
        AND (:end IS NULL OR a.activity_date <= :end)
        AND (:orgId IS NULL OR hb.org_id = :orgId)
""", nativeQuery = true)
    Long countActivities(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId
    );


    @Query(value = """
        SELECT
        CAST(EXTRACT(YEAR FROM a.activity_date) AS INT) AS year,
        CAST(EXTRACT(MONTH FROM a.activity_date) AS INT) AS month,
        COALESCE(SUM(w.weight_kg), 0) AS totalKg,
        COUNT(DISTINCT a.activity_id) AS activityCount
        FROM activity a
        LEFT JOIN waste w ON w.activity_id = a.activity_id
        LEFT JOIN hosted_by hb ON hb.activity_id = a.activity_id
        WHERE (:start IS NULL OR a.activity_date >= :start)
        AND (:end IS NULL OR a.activity_date <= :end)
        AND (:orgId IS NULL OR hb.org_id = :orgId)
        GROUP BY year, month
        ORDER BY year, month
""", nativeQuery = true)
    List<MonthlyAggRow> monthlySeries(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId
    );
}
