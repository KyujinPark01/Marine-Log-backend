package domain.backend.location.service;

import domain.backend.activity.domain.Activity;
import domain.backend.location.domain.Location;
import domain.backend.location.domain.LocationRepository;
import domain.backend.location.dto.request.LocationWeightedRequest;
import domain.backend.location.dto.response.LocationAggregation;
import domain.backend.location.dto.request.LocationRequest;
import domain.backend.location.dto.response.LocationSpecific;
import domain.backend.location.dto.response.LocationWeightedResponse;
import domain.backend.location.projection.LocationAggregationProjection;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {
    private final LocationRepository locationRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public Location saveForActivity(Activity activity, double lat, double lng) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity cannot be null");
        }
        // 1️⃣ geometry 생성
        Point point = geometryFactory.createPoint(new Coordinate(lng, lat));

        // 2️⃣ Builder로 latitude, longitude를 넘겨 geometry 초기화 (엔티티 그대로 사용)
        Location location = Location.builder()
                .latitude(lat)
                .longitude(lng)
                .build();

        // 3️⃣ Builder는 geometry를 내부에서 생성하지만, activity 연결은 직접 세팅
        //    (MapsId 관계라 activity_id가 PK로 매핑됨)
        location.setActivity(activity);

        // 4️⃣ geometryFactory로 만든 Point를 직접 교체 (Builder에서 만들어진 geometry 덮어쓰기)
        //    (이렇게 하면 geometry를 덮어쓸 수 있음)
        try {
            var field = Location.class.getDeclaredField("geometry");
            field.setAccessible(true);
            field.set(location, point);
        } catch (Exception e) {
            throw new RuntimeException("geometry 필드 접근 실패", e);
        }

        // 5️⃣ 저장
        Location saved = locationRepository.save(location);

        System.out.printf("✅ Location 저장 완료 → activityId=%d, POINT(%f %f)%n",
                activity.getActivityId(), lng, lat);

        return saved;
    }


    @Transactional(readOnly = true)
    public List<LocationAggregation> getAggregatedData(String bound) {
        List<LocationAggregationProjection> projections = switch (bound.toLowerCase()) {
            case "national" -> locationRepository.getAggregatedNational();
            case "broad" -> locationRepository.getAggregatedBroad();
            default -> throw new IllegalArgumentException("Invalid bound");
        };

        return projections.stream()
                .map(projection -> LocationAggregation.builder()
                        .region(projection.getRegion())
                        .counts(projection.getCounts())
                        .latitude(projection.getLatitude())
                        .longitude(projection.getLongitude())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LocationSpecific> getSpecificData(LocationRequest locationRequest) {
        LocalDate startDate = (locationRequest.getStartDate() != null) ? locationRequest.getStartDate() : LocalDate.of(1900, 1, 1);
        LocalDate endDate = (locationRequest.getEndDate() != null) ? locationRequest.getEndDate() : LocalDate.now();

        return locationRepository.getSpecific(
                locationRequest.getSwLat(),
                locationRequest.getSwLng(),
                locationRequest.getNeLat(),
                locationRequest.getNeLng(),
                startDate,
                endDate,
                locationRequest.getOrgId()
                ).stream()
                .map(projection -> LocationSpecific.builder()
                        .activityId(projection.getActivityId())
                        .activityName(projection.getActivityName())
                        .activityDate(projection.getActivityDate())
                        .latitude(projection.getLatitude())
                        .longitude(projection.getLongitude())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LocationWeightedResponse> getWeightedData(LocationWeightedRequest locationWeightedRequest) {
        LocalDate startDate = (locationWeightedRequest.getStartDate() != null) ? locationWeightedRequest.getStartDate() : LocalDate.of(1900, 1, 1);
        LocalDate endDate = (locationWeightedRequest.getEndDate() != null) ? locationWeightedRequest.getEndDate() : LocalDate.now();

        return locationRepository.getWeighted(startDate, endDate).stream()
                .map(projection -> LocationWeightedResponse.builder()
                        .longitude(projection.getLongitude())
                        .latitude(projection.getLatitude())
                        .weight(projection.getWeight())
                        .build())
                .toList();
    }
}
