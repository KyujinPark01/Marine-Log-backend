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
            SELECT region1depth_name as region,
            COUNT(*) AS counts,
            AVG(ST_X(geometry)) AS longitude,
            AVG(ST_Y(geometry)) AS latitude
            FROM location
            GROUP BY region1depth_name;
            """, nativeQuery = true)
    List<LocationAggregationProjection> getAggregatedNational();

    @Query(value = """
        SELECT region2depth_name as region,
        COUNT(*) AS counts,
        AVG(ST_X(geometry)) AS longitude,
        AVG(ST_Y(geometry)) AS latitude
        FROM location
        GROUP BY region2depth_name;
    """, nativeQuery = true)
    List<LocationAggregationProjection> getAggregatedBroad();

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
        WITH grid AS (
            SELECT DISTINCT ST_SnapToGrid(l.geometry, 0.1) AS geometry
            FROM location l
        )
        SELECT
            ST_X(grid.geometry) AS longitude,
            ST_Y(grid.geometry) AS latitude,
            COUNT(*) AS weight
        FROM location l
        JOIN grid
            ON ST_SnapToGrid(l.geometry, 0.1) = grid.geometry
        JOIN activity a
            ON a.activity_id = l.activity_id
        WHERE a.activity_date BETWEEN :startDate AND :endDate
        GROUP BY grid.geometry;
""",  nativeQuery = true)
    List<LocationWeightedProjection> getWeighted(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
