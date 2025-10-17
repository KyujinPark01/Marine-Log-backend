package domain.backend.location.service;

import domain.backend.location.domain.LocationRepository;
import domain.backend.location.dto.LocationAggregationDto;
import domain.backend.location.dto.LocationRequestDto;
import domain.backend.location.dto.LocationSpecificDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;


    public List<LocationAggregationDto> getAggregatedNationalData() {
        return locationRepository.getAggregatedNational().stream()
                .map(repository -> LocationAggregationDto.builder()
                        .region(repository.getRegion())
                        .counts(repository.getCounts())
                        .latitude(repository.getLatitude())
                        .longitude(repository.getLongitude())
                        .build())
                .toList();
    }

    public List<LocationAggregationDto> getAggregatedBroadData() {
        return locationRepository.getAggregatedBroad().stream()
                .map(repository -> LocationAggregationDto.builder()
                        .region(repository.getRegion())
                        .counts(repository.getCounts())
                        .latitude(repository.getLatitude())
                        .longitude(repository.getLongitude())
                        .build())
                .toList();
    }

    public List<LocationAggregationDto> getAggregatedRegionalData() {
        return locationRepository.getAggregatedRegional().stream()
                .map(repository -> LocationAggregationDto.builder()
                        .region(repository.getRegion())
                        .counts(repository.getCounts())
                        .latitude(repository.getLatitude())
                        .longitude(repository.getLongitude())
                        .build())
                .toList();
    }

    public List<LocationSpecificDto> getSpecificData(LocationRequestDto locationRequestDto) {
        return locationRepository.getSpecific(
                locationRequestDto.getSwLat(),
                locationRequestDto.getSwLng(),
                locationRequestDto.getNeLat(),
                locationRequestDto.getNeLng()
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
