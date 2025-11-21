package domain.backend.dashboard.repository;


import domain.backend.dashboard.dto.response.StatSummaryResponse;
import domain.backend.dashboard.projection.MonthlyAggRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import domain.backend.activity.domain.Activity;

import java.time.LocalDate;
import java.util.List;

public interface ActivityStatRepository extends JpaRepository<Activity, Long> {

    @Query(value = """
        WITH filtered_activity AS (
            SELECT DISTINCT a.activity_id
            FROM activity a
            LEFT JOIN hosted_by hb ON hb.activity_id = a.activity_id
            WHERE a.activity_date BETWEEN :start AND :end
              AND hb.org_id = :orgId
        ),
        activity_count AS (
            SELECT COUNT(*) AS activityCount FROM filtered_activity
        ),
        waste_agg AS (
            SELECT
                COALESCE(SUM(w.weight_kg), 0) AS totalKg,
                COALESCE(SUM(w.volume_l), 0) AS totalL
            FROM waste w
            WHERE w.activity_id IN (SELECT activity_id FROM filtered_activity)
        )
        SELECT
            activity_count.activityCount AS activityCount,
            waste_agg.totalKg AS totalKg,
            waste_agg.totalL AS totalL,
            CASE
                WHEN activity_count.activityCount = 0 THEN 0
                ELSE waste_agg.totalKg / activity_count.activityCount
            END AS avgKgPerActivity
        FROM activity_count
        CROSS JOIN waste_agg
        """, nativeQuery = true)
    StatSummaryResponse summarizedStat(
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
        WHERE a.activity_date BETWEEN :start AND :end
            AND hb.org_id = :orgId
        GROUP BY activityYear, activityMonth
        ORDER BY activityYear, activityMonth
        """, nativeQuery = true)
    List<MonthlyAggRow> monthlySeries(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId
    );
}
