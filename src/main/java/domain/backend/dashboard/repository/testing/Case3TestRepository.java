package domain.backend.dashboard.repository.testing;

import domain.backend.dashboard.projection.SumWasteView;
import domain.backend.waste.domain.Waste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface Case3TestRepository extends JpaRepository<Waste, Long> {
    @Query(value = """
        SELECT COUNT(DISTINCT a.activity_id)
        FROM activity a
        LEFT JOIN hosted_by hb ON hb.activity_id = a.activity_id
        WHERE (a.activity_date BETWEEN :start AND :end)
            AND hb.org_id = :orgId      
        """, nativeQuery = true)
    Long countActivities(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId
    );

    @Query(value = """
        SELECT 
            COALESCE(SUM(w.weight_kg),0) AS totalKg, 
            COALESCE(SUM(w.volume_l),0) AS totalL
        FROM waste w
        JOIN activity a ON a.activity_id = w.activity_id
        LEFT JOIN hosted_by hb ON hb.activity_id = a.activity_id
        WHERE (a.activity_date BETWEEN :start AND :end)
            AND hb.org_id = :orgId
        """, nativeQuery = true)
    SumWasteView sumWaste(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId
    );

}
