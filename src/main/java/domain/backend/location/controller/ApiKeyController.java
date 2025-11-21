package domain.backend.location.controller;

import domain.backend.common.dto.ApiResponse;
import domain.backend.location.dto.response.MapInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(
        name = "API Key Config",
        description = "Google Maps 연동을 위한 API Key 및 Map ID 제공 API"
)
@RestController
@RequestMapping("/api")
public class ApiKeyController {
    @Value("${google.api.key}")
    private String googleApiKey;

    @Value("${google.maps.id}")
    private String googleMapsId;
    @Operation(summary = "Google Maps 설정 조회", description = "Google Maps 초기화를 위한 API Key와 Map ID를 제공합니다.")
    @GetMapping("/key/google-maps-config")
    public ResponseEntity<ApiResponse<MapInfoResponse>> getGoogleMapsInfo() {
        return ResponseEntity.ok(ApiResponse.ok(new MapInfoResponse(googleApiKey, googleMapsId)));
    }
}
