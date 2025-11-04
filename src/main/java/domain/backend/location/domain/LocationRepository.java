package domain.backend.location.domain;

import domain.backend.activity.domain.Activity;
import domain.backend.location.dto.request.LocationWeightedRequest;
import domain.backend.location.projection.LocationAggregationProjection;
import domain.backend.location.projection.LocationSpecificProjection;
import domain.backend.location.projection.LocationWeightedProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface LocationRepository extends JpaRepository<Location, Activity> {
    @Query(value = """
        SELECT l.region1depth_name as region,
            COUNT(*) AS counts,
            AVG(ST_X(l.geometry)) AS longitude,
            AVG(ST_Y(l.geometry)) AS latitude
        FROM location l
        JOIN activity a
        ON l.activity_id = a.activity_id
        WHERE a.status = 'PUBLISHED'
        GROUP BY l.region1depth_name;
            """, nativeQuery = true)
    List<LocationAggregationProjection> getAggregatedNational();

    @Query(value = """
        SELECT l.region2depth_name as region,
            COUNT(*) AS counts,
            AVG(ST_X(l.geometry)) AS longitude,
            AVG(ST_Y(l.geometry)) AS latitude
        FROM location l
        JOIN activity a
        ON l.activity_id = a.activity_id
        WHERE a.status = 'PUBLISHED'
        GROUP BY l.region2depth_name;
    """, nativeQuery = true)
    List<LocationAggregationProjection> getAggregatedBroad();

    @Deprecated(forRemoval = true)
    @Query(value = """
        SELECT region3depth_name as region,
            COUNT(*) AS counts,
            AVG(ST_X(geometry)) AS longitude,
            AVG(ST_Y(geometry)) AS latitude
        FROM location
        GROUP BY region3depth_name;
    """, nativeQuery = true)
    List<LocationAggregationProjection> getAggregatedRegional();

    @Query(value = """
        SELECT a.activity_id AS activityId,
            a.name AS activityName,
            a.activity_date AS activityDate,
            ST_X(l.geometry) AS longitude,
            ST_Y(l.geometry) AS latitude
        FROM location l
        JOIN activity a
        ON l.activity_id = a.activity_id
        LEFT JOIN hosted_by h
        ON h.activity_id = a.activity_id
        WHERE l.geometry && ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326)
        AND a.status = 'PUBLISHED'
        AND a.activity_date BETWEEN :startDate AND :endDate
        AND (:orgId IS NULL OR h.org_id = :orgId);
    """, nativeQuery = true)
    List<LocationSpecificProjection> getSpecific(
            @Param("swLat") Double swLat,
            @Param("swLng") Double swLng,
            @Param("neLat") Double neLat,
            @Param("neLng") Double neLng,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("orgId") Long orgId
    );

    @Query(value = """
        WITH filtered AS (
            SELECT l.geometry, a.weight_kg
            FROM location l
            JOIN activity a ON a.activity_id = l.activity_id
            WHERE a.activity_date BETWEEN :startDate AND :endDate
        ),
        grid AS (
            SELECT
                ST_SnapToGrid(geometry, 0.1) AS snapped,
                SUM(weight_kg) AS total_weight
            FROM filtered
            GROUP BY ST_SnapToGrid(geometry, 0.1)
        ),
        stats AS (
            SELECT
                MIN(total_weight) AS min_w,
                MAX(total_weight) AS max_w
            FROM grid
        )
        SELECT
            ST_X(snapped) AS longitude,
            ST_Y(snapped) AS latitude,
            CASE
                WHEN stats.max_w = stats.min_w THEN 5
                ELSE 1 + 9 * (total_weight - stats.min_w) / (stats.max_w - stats.min_w)
            END AS weight
        FROM grid, stats;
""",  nativeQuery = true)
    List<LocationWeightedProjection> getWeighted(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
