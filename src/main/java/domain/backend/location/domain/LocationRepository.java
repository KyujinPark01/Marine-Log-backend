package domain.backend.location.domain;

import domain.backend.activity.domain.Activity;
import domain.backend.location.dto.LocationAggregationDto;
import domain.backend.location.projection.LocationAggregationProjection;
import domain.backend.location.projection.LocationSpecificProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Objects;


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
        SELECT a.id AS activityId,
        a.name AS activityName,
        a.start_datetime AS activityDate,
        ST_X(l.geometry) AS longitude,
        ST_Y(l.geometry) AS latitude
        FROM location l
        JOIN activity a
        ON l.activity_id = a.id
        WHERE l.geometry && ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326);
    """, nativeQuery = true)
    List<LocationSpecificProjection> getSpecific(
            @Param("swLat") Double swLat,
            @Param("swLng") Double swLng,
            @Param("neLat") Double neLat,
            @Param("neLng") Double neLng);
}
