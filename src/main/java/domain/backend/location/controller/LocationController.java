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

    @GetMapping("/activity/aggregate")
    public List<LocationAggregationDto> getAggregated(@PathVariable String bound) {
        return locationService.getAggregatedData(bound);
    }

    @PostMapping("/activity/specific")
    public List<LocationSpecificDto> getAggregatedSpecific(@RequestBody LocationRequestDto locationRequestDto) {
        return locationService.getSpecificData(locationRequestDto);
    }
}
