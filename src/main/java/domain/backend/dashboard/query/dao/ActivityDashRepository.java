package domain.backend.dashboard.query.dao;


import domain.backend.dashboard.query.dto.MonthlyAggRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import domain.backend.activity.domain.Activity;

import java.time.LocalDate;
import java.util.List;

public interface ActivityDashRepository extends JpaRepository<Activity, Long> {

    @Query(value = """
        SELECT COUNT(DISTINCT a.activity_id)
        FROM activity a
        LEFT JOIN hosted_by hb ON hb.activity_id = a.activity_id
        WHERE (CAST(:start AS DATE) IS NULL OR a.activity_date >= :start)
          AND (CAST(:end AS DATE) IS NULL OR a.activity_date <= :end)
          AND (CAST(:orgId AS BIGINT) IS NULL OR hb.org_id = :orgId)
        """, nativeQuery = true)
    Long countActivities(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId
    );

    @Query(value = """
        SELECT
            EXTRACT(YEAR FROM a.activity_date) AS activityYear,
            EXTRACT(MONTH FROM a.activity_date) AS activityMonth,
            COALESCE(SUM(w.weight_kg), 0) AS totalKg,
            COUNT(DISTINCT a.activity_id) AS activityCount
        FROM activity a
        LEFT JOIN waste w ON w.activity_id = a.activity_id
        LEFT JOIN hosted_by hb ON hb.activity_id = a.activity_id
        WHERE (CAST(:start AS DATE) IS NULL OR a.activity_date >= :start)
          AND (CAST(:end AS DATE) IS NULL OR a.activity_date <= :end)
          AND (CAST(:orgId AS BIGINT) IS NULL OR hb.org_id = :orgId)
        GROUP BY activityYear, activityMonth
        ORDER BY activityYear, activityMonth
        """, nativeQuery = true)
    List<MonthlyAggRow> monthlySeries(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId
    );
}
