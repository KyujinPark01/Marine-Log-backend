package com.example.global.apikey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleGeocodingService {

    @Value("${google.api.key}")
    private String googleApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> getAdministrativeRegion(double lat, double lng) {
        try {
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&language=ko&key=%s",
                    lat, lng, googleApiKey
            );

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            var results = (java.util.List<Map<String, Object>>) response.getBody().get("results");

            if (results == null || results.isEmpty()) {
                log.warn("⚠️ Google Geocoding 결과 없음: {}, {}", lat, lng);
                return Map.of("region1", "미상", "region2", "", "region3", "");
            }

            var components = (java.util.List<Map<String, Object>>) results.get(0).get("address_components");

            String region1 = "", region2 = "", region3 = "";
            for (Map<String, Object> comp : components) {
                var types = (java.util.List<String>) comp.get("types");
                String name = (String) comp.get("long_name");

                if (types.contains("administrative_area_level_1")) region1 = name; // 시/도
                else if (types.contains("administrative_area_level_2")) region2 = name; // 시/군/구
                else if (types.contains("sublocality_level_1")) region3 = name; // 읍/면/동
            }

            return Map.of("region1", region1, "region2", region2, "region3", region3);

        } catch (Exception e) {
            log.error("❌ Geocoding API 실패", e);
            return Map.of("region1", "미상", "region2", "", "region3", "");
        }
    }
}
