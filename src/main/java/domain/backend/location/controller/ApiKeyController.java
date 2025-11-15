package domain.backend.location.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/key")
public class ApiKeyController {
    @Value("${google.api.key}")
    private String googleApiKey;

    @Value("${google.maps.id}")
    private String googleMapsId;

    @GetMapping("/google-maps-config")
    public ResponseEntity<MapInfoResponse> getGoogleMapsInfo() {
        return ResponseEntity.ok(new MapInfoResponse(googleApiKey, googleMapsId));
    }

    public record MapInfoResponse(String apiKey, String mapId) {}
}
