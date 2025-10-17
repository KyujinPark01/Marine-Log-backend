package domain.backend.location.controller;

import domain.backend.location.dto.LocationAggregationDto;
import domain.backend.location.dto.LocationRequestDto;
import domain.backend.location.dto.LocationSpecificDto;
import domain.backend.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/activity/national")
    public List<LocationAggregationDto> getAggregatedNational() {
        return locationService.getAggregatedNationalData();
    }

    @GetMapping("/activity/broad")
    public List<LocationAggregationDto> getAggregatedBroad() {
        return locationService.getAggregatedBroadData();
    }

    @GetMapping("/activity/regional")
    public List<LocationAggregationDto> getAggregatedRegional() {
        return locationService.getAggregatedRegionalData();
    }

    @PostMapping("/activity/specific")
    public List<LocationSpecificDto> getAggregatedSpecific(@RequestBody LocationRequestDto locationRequestDto) {
        return locationService.getSpecificData(locationRequestDto);
    }
}
