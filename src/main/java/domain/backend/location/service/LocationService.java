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
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LocationService {
    private final LocationRepository locationRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Value("${google.api.keys}")
    private String googleApiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public Location saveForActivity(Activity activity, double lat, double lng) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity cannot be null");
        }
        // 1️⃣ Google Geocoding API 호출
        Map<String, String> region = getAdministrativeRegion(lat, lng);
        String region1 = region.getOrDefault("region1", "미상");
        String region2 = region.getOrDefault("region2", "");
        String region3 = region.getOrDefault("region3", "");

        // 1️⃣ geometry 생성
        Point point = geometryFactory.createPoint(new Coordinate(lng, lat));

        // 2️⃣ Builder로 latitude, longitude를 넘겨 geometry 초기화 (엔티티 그대로 사용)
        Location location = Location.builder()
                .latitude(lat)
                .longitude(lng)
                .region1depthName(region1)
                .region2depthName(region2)
                .region3depthName(region3)
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

    private Map<String, String> getAdministrativeRegion(double lat, double lng) {
        log.info("🌍 [Geocoding 요청 시작] 위도: {}, 경도: {}", lat, lng);

        try {
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&language=ko&key=%s",
                    lat, lng, googleApiKey
            );
            log.debug("📡 Google Geocoding API 요청 URL: {}", url);

            long start = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            long elapsed = System.currentTimeMillis() - start;

            HttpStatus status = (HttpStatus) response.getStatusCode();
            log.info("✅ [Geocoding 응답 수신] HTTP 상태: {}, 소요시간: {}ms", status, elapsed);

            if (!status.is2xxSuccessful()) {
                log.error("❌ Geocoding API 호출 실패 (HTTP {})", status.value());
                return blankRegions();
            }

            Map<String, Object> body = response.getBody();
            if (body == null) {
                log.error("❌ Geocoding 응답 body가 null입니다.");
                return blankRegions();
            }

            String statusText = (String) body.get("status");
            List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");

            if (results == null || results.isEmpty()) {
                log.warn("⚠️ Geocoding 결과 없음 (status={}): {}, {}", statusText, lat, lng);
                return blankRegions();
            }

            List<Map<String, Object>> components =
                    (List<Map<String, Object>>) results.get(0).get("address_components");

            if (components == null || components.isEmpty()) {
                log.warn("⚠️ address_components 비어 있음: {}, {}", lat, lng);
                return blankRegions();
            }

            // ✅ 시/도 ~ 세부주소까지 5단계 행정구역 추출
            String region1 = "", region2 = "", region3 = "", region4 = "", region5 = "";

            for (Map<String, Object> comp : components) {
                List<String> types = (List<String>) comp.get("types");
                String name = (String) comp.get("long_name");

                // 디버깅 로그
                log.debug("📍 Google Types: {}, Name: {}", types, name);

                if (types.contains("administrative_area_level_1")) {
                    region1 = name;
                } else if (types.contains("administrative_area_level_2") || types.contains("sublocality_level_1")) {
                    region2 = name;
                } else if (types.contains("administrative_area_level_3") || types.contains("sublocality_level_2")) {
                    region3 = name;
                } else if (types.contains("sublocality_level_3") || types.contains("neighborhood")) {
                    region4 = name;
                } else if (types.contains("route") || types.contains("premise")) {
                    region5 = name;
                }
            }

            log.info("🏙️ [행정구역 추출 완료]");
            log.info("  region1(시/도): {}", region1);
            log.info("  region2(시/군/구): {}", region2);
            log.info("  region3(읍/면/동): {}", region3);
            log.info("  region4(리/세부지역): {}", region4);
            log.info("  region5(도로명/건물): {}", region5);

            return Map.of(
                    "region1", region1,
                    "region2", region2,
                    "region3", region3,
                    "region4", region4,
                    "region5", region5
            );

        } catch (Exception e) {
            log.error("❌ [Geocoding 예외 발생] 위도={}, 경도={}, 메시지={}", lat, lng, e.getMessage(), e);
            return blankRegions();
        }
    }

    /**
     * 빈 Map 기본값 반환 (예외/실패 시)
     */
    private Map<String, String> blankRegions() {
        return Map.of(
                "region1", "미상",
                "region2", "",
                "region3", "",
                "region4", "",
                "region5", ""
        );
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
