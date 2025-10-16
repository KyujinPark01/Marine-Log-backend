package domain.backend.location.domain;

import domain.backend.activity.domain.Activity;
import domain.backend.location.dto.LocationAggregationDto;
import domain.backend.location.projection.LocationAggregationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
