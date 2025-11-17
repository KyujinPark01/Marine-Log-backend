package domain.backend.location;

import domain.backend.location.dto.response.GeocodeResponse;
import domain.backend.location.service.LocationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KakaoGeocodeTest {
    @Autowired
    private LocationService locationService;

    @Test
    void doGeocode() {
        GeocodeResponse response = locationService.getRegion(37.55467884, 126.9706069);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getDocuments());
        Assertions.assertNotNull(response.getDocuments().getFirst());

        System.out.println(response.getDocuments().getFirst().getAddressName());
    }
}
