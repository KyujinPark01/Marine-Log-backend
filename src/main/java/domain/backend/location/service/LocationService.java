package domain.backend.location.service;

import domain.backend.location.domain.LocationRepository;
import domain.backend.location.dto.request.LocationWeightedRequest;
import domain.backend.location.dto.response.LocationAggregation;
import domain.backend.location.dto.request.LocationRequest;
import domain.backend.location.dto.response.LocationSpecific;
import domain.backend.location.dto.response.LocationWeightedResponse;
import domain.backend.location.projection.LocationAggregationProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {
    private final LocationRepository locationRepository;

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
