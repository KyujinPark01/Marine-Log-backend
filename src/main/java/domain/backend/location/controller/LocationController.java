package domain.backend.location.controller;

import domain.backend.location.dto.request.LocationWeightedRequest;
import domain.backend.location.dto.response.LocationAggregation;
import domain.backend.location.dto.request.LocationRequest;
import domain.backend.location.dto.response.LocationSpecific;
import domain.backend.location.dto.response.LocationWeightedResponse;
import domain.backend.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/aggregate")
    public List<LocationAggregation> getAggregated(@RequestParam String bound) {
        return locationService.getAggregatedData(bound);
    }

    @PostMapping("/specific")
    public List<LocationSpecific> getAggregatedSpecific(@RequestBody LocationRequest locationRequestDto) {
        return locationService.getSpecificData(locationRequestDto);
    }

    @PostMapping("/weighted")
    public List<LocationWeightedResponse> getWeighted(@RequestBody LocationWeightedRequest locationWeightedRequest) {
        return locationService.getWeightedData(locationWeightedRequest);
    }

}
