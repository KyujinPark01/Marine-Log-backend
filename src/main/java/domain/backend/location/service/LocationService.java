package domain.backend.location.service;

import domain.backend.location.domain.LocationRepository;
import domain.backend.location.dto.LocationAggregationDto;
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


}
