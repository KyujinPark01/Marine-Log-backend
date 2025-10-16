package domain.backend.location.controller;

import domain.backend.location.dto.LocationAggregationDto;
import domain.backend.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
