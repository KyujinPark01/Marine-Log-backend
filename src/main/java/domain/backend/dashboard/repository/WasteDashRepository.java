package domain.backend.dashboard.repository;


import domain.backend.dashboard.projection.SumWasteView;
import domain.backend.dashboard.dto.MaterialShareRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WasteDashRepository extends Repository<domain.backend.common.entity.Waste, Long> {

    @Query(value = """
            SELECT COALESCE(SUM(w.weight_kg),0) AS totalKg, COALESCE(SUM(w.volume_l),0) AS totalL
            FROM waste w
            JOIN activity a ON a.activity_id = w.activity_id
            LEFT JOIN hosted_by hb ON hb.activity_id = a.activity_id
            WHERE (:start IS NULL OR a.activity_date >= :start)
            AND (:end IS NULL OR a.activity_date <= :end)
            AND (:orgId IS NULL OR hb.org_id = :orgId)
            """,
            nativeQuery = true)
    SumWasteView sumWaste(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("orgId") Long orgId);


    @Query(value = """
            SELECT COALESCE(w.type, 'UNKNOWN') AS material, COALESCE(SUM(w.weight_kg),0) AS kg
            FROM waste w
            JOIN activity a ON a.activity_id = w.activity_id
            LEFT JOIN hosted_by hb ON hb.activity_id = a.activity_id
            WHERE (:start IS NULL OR a.activity_date >= :start)
            AND (:end IS NULL OR a.activity_date <= :end)
            AND (:orgId IS NULL OR hb.org_id = :orgId)
            GROUP BY COALESCE(w.type, 'UNKNOWN')
            ORDER BY kg DESC
            """,
            nativeQuery = true)
    List<MaterialShareRow> materialBreakdown(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId
    );
}

