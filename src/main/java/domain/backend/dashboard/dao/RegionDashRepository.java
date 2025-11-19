package domain.backend.dashboard.dao;

import domain.backend.activity.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionDashRepository extends JpaRepository<Activity, Long> {

    @Query(value = """
        SELECT a.activity_id FROM activity a
        JOIN location l ON a.activity_id = l.activity_id
        WHERE a.status = 'PUBLISHED'
        AND l.geometry && ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326)
    """, nativeQuery = true)
    List<Long> findActivityIdsInBounds(@Param("swLat") Double swLat, @Param("swLng") Double swLng,
                                       @Param("neLat") Double neLat, @Param("neLng") Double neLng);

    @Query(value = """
        SELECT COALESCE(SUM(w.weight_kg), 0.0) as totalWeightKg, COUNT(w.waste_id)::int as wasteCount
        FROM waste w
        WHERE w.activity_id IN :activityIds
    """, nativeQuery = true)
    WasteSummaryInRegion getSummaryInActivities(@Param("activityIds") List<Long> activityIds);

    @Query(value = """
        SELECT w.type as materialType, COALESCE(SUM(w.weight_kg), 0.0) as totalKg
        FROM waste w
        WHERE w.activity_id IN :activityIds AND w.type IS NOT NULL
        GROUP BY w.type
    """, nativeQuery = true)
    List<MaterialBreakdownInRegion> getMaterialBreakdownInActivities(@Param("activityIds") List<Long> activityIds);

    @Query(value = """
        SELECT TO_CHAR(a.activity_date, 'YYYY-MM') as month, COALESCE(SUM(w.weight_kg), 0.0) as totalKg
        FROM waste w
        JOIN activity a ON w.activity_id = a.activity_id
        WHERE w.activity_id IN :activityIds
        GROUP BY TO_CHAR(a.activity_date, 'YYYY-MM')
        ORDER BY month
    """, nativeQuery = true)
    List<MonthlyTrendInRegion> getMonthlyTrendInActivities(@Param("activityIds") List<Long> activityIds);


    interface WasteSummaryInRegion {
        Double getTotalWeightKg();
        Integer getWasteCount();
    }

    interface MaterialBreakdownInRegion {
        String getMaterialType();
        Double getTotalKg();
    }

    interface MonthlyTrendInRegion {
        String getMonth();
        Double getTotalKg();
    }
}
