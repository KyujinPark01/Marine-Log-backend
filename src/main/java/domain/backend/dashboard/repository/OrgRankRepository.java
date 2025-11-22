package domain.backend.dashboard.repository;

import domain.backend.dashboard.projection.OrgRankingRow;
import domain.backend.organization.domain.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrgRankRepository extends JpaRepository<Organization, Long> {

    @Query(value = """
        SELECT  
            o.org_id AS orgId, 
            o.name AS orgName, 
            COALESCE(SUM(w.weight_kg),0) AS totalKg
        FROM organization o
        JOIN hosted_by hb ON hb.org_id = o.org_id
        JOIN activity a ON a.activity_id = hb.activity_id
        LEFT JOIN waste w ON w.activity_id = a.activity_id
        WHERE (a.activity_date BETWEEN :start AND :end)
        GROUP BY o.org_id, o.name
        ORDER BY totalKg DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<OrgRankingRow> orgRanking(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId,
            @Param("limit") int limit
    );
}
