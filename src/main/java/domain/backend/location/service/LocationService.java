package domain.backend.location.service;

import domain.backend.activity.domain.Activity;
import domain.backend.location.domain.Location;
import domain.backend.location.domain.LocationRepository;
import domain.backend.location.dto.request.LocationWeightedRequest;
import domain.backend.location.dto.response.GeocodeResponse;
import domain.backend.location.dto.response.LocationAggregation;
import domain.backend.location.dto.request.LocationRequest;
import domain.backend.location.dto.response.LocationSpecific;
import domain.backend.location.dto.response.LocationWeightedResponse;
import domain.backend.location.projection.LocationAggregationProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;
    private final RestClient restClient;

    private static final String KAKAO_GEOCODE_URL = "/v2/local/geo/coord2regioncode";

    @Transactional
    public Location saveForActivity(Activity activity, double lat, double lng) {
        if (Objects.isNull(activity)) {
            throw new IllegalArgumentException("Activity cannot be null");
        }

        GeocodeResponse.Document regions = getRegion(lat, lng).getDocuments().getFirst();
        // Todo: 외부 api 요청은 transaction 외부에 있는 것이 권장됨. 리팩토링 필요

        Location location = Location.builder()
                .activity(activity)
                .latitude(lat)
                .longitude(lng)
                .region1depthName(regions.getRegion1depthName())
                .region2depthName(regions.getRegion2depthName())
                .region3depthName(regions.getRegion3depthName())
                .address(regions.getAddressName())
                .build();

        return locationRepository.save(location);
    }

    public GeocodeResponse getRegion(double lat, double lng) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(KAKAO_GEOCODE_URL)
                        .queryParam("x", lng)
                        .queryParam("y", lat)
                        .build()
                )
                .retrieve()
                .body(GeocodeResponse.class);
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
