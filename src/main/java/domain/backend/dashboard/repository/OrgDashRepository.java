package domain.backend.dashboard.repository;

import domain.backend.dashboard.dto.OrgRankingRow;
import domain.backend.organization.domain.entity.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrgDashRepository extends Repository<Organization, Long> {
    @Query(value = """
            SELECT o.org_id AS orgId, o.name AS orgName, COALESCE(SUM(w.weight_kg),0) AS totalKg
            FROM organization o
            JOIN hosted_by hb ON hb.org_id = o.org_id
            JOIN activity a ON a.activity_id = hb.activity_id
            LEFT JOIN waste w ON w.activity_id = a.activity_id
            WHERE (:start IS NULL OR a.activity_date >= :start)
            AND (:end IS NULL OR a.activity_date <= :end)
            AND (:orgId IS NULL OR o.org_id = :orgId)
            GROUP BY o.org_id, o.name
            ORDER BY totalKg DESC
            LIMIT :limit
            """,
            nativeQuery = true)
    List<OrgRankingRow> orgRanking(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("orgId") Long orgId,
            @Param("limit") int limit
    );
}