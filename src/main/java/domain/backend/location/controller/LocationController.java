package domain.backend.location.controller;

import domain.backend.common.dto.ApiResponse;
import domain.backend.location.dto.request.LocationWeightedRequest;
import domain.backend.location.dto.response.LocationAggregation;
import domain.backend.location.dto.request.LocationRequest;
import domain.backend.location.dto.response.LocationSpecific;
import domain.backend.location.dto.response.LocationWeightedResponse;
import domain.backend.location.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(
        name = "Location API",
        description = "지역별 활동 통계 및 위치 데이터 관련 API"
)
@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "지역별 집계 데이터", description = "지정한 범위 내의 지역별 활동 집계 데이터를 조회합니다.")
    @GetMapping("/location/aggregate")
    public ResponseEntity<ApiResponse<List<LocationAggregation>>> getAggregated(
            @NotBlank(message = "집계 행정구역 단위를 명시해야 합니다")
            @RequestParam String bound
    ) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getAggregatedData(bound)));
    }

    @Operation(summary = "세부 위치 데이터", description = "입력한 조건에 따른 세부 위치 데이터를 조회합니다.")
    @PostMapping("/location/specific")
    public ResponseEntity<ApiResponse<List<LocationSpecific>>> getAggregatedSpecific(
            @Valid
            @RequestBody LocationRequest locationRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getSpecificData(locationRequestDto)));
    }

    @Operation(summary = "가중치 기반 히트맵 데이터", description = "활동 밀도를 시각화하기 위한 가중치 데이터를 조회합니다.")
    @PostMapping("/location/weighted")
    public ResponseEntity<ApiResponse<List<LocationWeightedResponse>>> getWeighted(
            @Valid
            @RequestBody LocationWeightedRequest locationWeightedRequest
    ) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getWeightedData(locationWeightedRequest)));
    }
}
