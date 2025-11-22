package domain.backend.dashboard.repository.testing;

import domain.backend.activity.domain.Activity;
import domain.backend.dashboard.dto.response.StatSummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface Case2TestRepository extends JpaRepository<Activity, Long> {
    @Query(value = """
        SELECT
            COUNT(*) AS activityCount,
            COALESCE(SUM(wagg.totalKg), 0) AS totalKg,
            COALESCE(SUM(wagg.totalL), 0) AS totalL,
            NULL AS avgKgPerActivity
        FROM (
            SELECT DISTINCT a.activity_id
            FROM activity a
            JOIN hosted_by hb ON hb.activity_id = a.activity_id
            WHERE a.activity_date BETWEEN :start AND :end
              AND hb.org_id = :orgId
        ) filtered_activity
        LEFT JOIN (
            SELECT
                activity_id,
                SUM(weight_kg) AS totalKg,
                SUM(volume_l) AS totalL
            FROM waste
            GROUP BY activity_id
        ) wagg ON wagg.activity_id = filtered_activity.activity_id
        """, nativeQuery = true)
    StatSummaryResponse summarizedStat(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId
    );
}
