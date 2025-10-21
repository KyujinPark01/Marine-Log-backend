package domain.backend.location.service;

import domain.backend.location.domain.LocationRepository;
import domain.backend.location.dto.LocationAggregationDto;
import domain.backend.location.dto.LocationRequestDto;
import domain.backend.location.dto.LocationSpecificDto;
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
    public List<LocationAggregationDto> getAggregatedData(String bound) {
        List<LocationAggregationProjection> projections = switch (bound.toLowerCase()) {
            case "national" -> locationRepository.getAggregatedNational();
            case "broad" -> locationRepository.getAggregatedBroad();
            case "regional" -> locationRepository.getAggregatedRegional();
            default -> throw new IllegalArgumentException("Invalid bound");
        };

        return projections.stream()
                .map(repository -> LocationAggregationDto.builder()
                        .region(repository.getRegion())
                        .counts(repository.getCounts())
                        .latitude(repository.getLatitude())
                        .longitude(repository.getLongitude())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LocationSpecificDto> getSpecificData(LocationRequestDto locationRequestDto) {
        LocalDate startDate = (locationRequestDto.getStartDate() != null) ? locationRequestDto.getStartDate() : LocalDate.of(1900, 1, 1);
        LocalDate endDate = (locationRequestDto.getEndDate() != null) ? locationRequestDto.getEndDate() : LocalDate.now();

        return locationRepository.getSpecific(
                locationRequestDto.getSwLat(),
                locationRequestDto.getSwLng(),
                locationRequestDto.getNeLat(),
                locationRequestDto.getNeLng(),
                startDate,
                endDate,
                locationRequestDto.getOrgId()
                ).stream()
                .map(repository -> LocationSpecificDto.builder()
                        .activityId(repository.getActivityId())
                        .activityName(repository.getActivityName())
                        .activityDate(repository.getActivityDate())
                        .latitude(repository.getLatitude())
                        .longitude(repository.getLongitude())
                        .build())
                .toList();
    }
}
