package domain.backend.location.controller;

import domain.backend.location.dto.request.LocationWeightedRequest;
import domain.backend.location.dto.response.LocationAggregation;
import domain.backend.location.dto.request.LocationRequest;
import domain.backend.location.dto.response.LocationSpecific;
import domain.backend.location.dto.response.LocationWeightedResponse;
import domain.backend.location.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(
        name = "Location API",
        description = "지역별 활동 통계 및 위치 데이터 관련 API"
)
@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @Operation(summary = "지역별 집계 데이터", description = "지정한 범위 내의 지역별 활동 집계 데이터를 조회합니다.")
    @GetMapping("/aggregate")
    public List<LocationAggregation> getAggregated(@RequestParam String bound) {
        return locationService.getAggregatedData(bound);
    }
    @Operation(summary = "세부 위치 데이터", description = "입력한 조건에 따른 세부 위치 데이터를 조회합니다.")
    @PostMapping("/specific")
    public List<LocationSpecific> getAggregatedSpecific(@RequestBody LocationRequest locationRequestDto) {
        return locationService.getSpecificData(locationRequestDto);
    }
    @Operation(summary = "가중치 기반 히트맵 데이터", description = "활동 밀도를 시각화하기 위한 가중치 데이터를 조회합니다.")
    @PostMapping("/weighted")
    public List<LocationWeightedResponse> getWeighted(@RequestBody LocationWeightedRequest locationWeightedRequest) {
        return locationService.getWeightedData(locationWeightedRequest);
    }

}
