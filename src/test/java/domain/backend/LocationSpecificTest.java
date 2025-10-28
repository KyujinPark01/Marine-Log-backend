package domain.backend;


import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.Status;
import domain.backend.activity.repository.ActivityRepository;
import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import domain.backend.location.domain.Location;
import domain.backend.location.domain.LocationRepository;
import domain.backend.location.dto.request.LocationRequest;
import domain.backend.location.dto.request.LocationWeightedRequest;
import domain.backend.location.projection.LocationSpecificProjection;
import domain.backend.location.projection.LocationWeightedProjection;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@SpringBootTest
@Transactional
public class LocationSpecificTest {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        int numEntities = 50;

        for (int i = 0; i < numEntities; i++) {
            User user = User.builder()
                    .name("User" + i)
                    .address("address" + i)
                    .email("email" + i)
                    .role(Role.USER)
                    .phoneNumber("phoneNumber" + i)
                    .refreshToken("refreshToken" + i)
                    .build();

            userRepository.save(user);

            Activity act = Activity.builder()
                    .name("Activity " + i)
                    .description("Description " + i)
                    .participants(random.nextInt(50))
                    .status(Status.PUBLISHED)
                    .activityDate(LocalDate.of(2010, 1, 1))
                    .createdBy(user)
                    .build();
            activityRepository.save(act);

            double lat = 35 + random.nextDouble(); // 예: 35~36
            double lng = 126 + random.nextDouble(); // 예: 126~127

            Location loc = Location.builder()
                    .activity(act)
                    .geometry(new org.locationtech.jts.geom.GeometryFactory().createPoint(new Coordinate(lng, lat)))
                    .region1depthName("Region" + (i % 10))
                    .region2depthName("SubRegion" + (i % 20))
                    .region3depthName("Detail" + (i % 50))
                    .address("Address " + i)
                    .build();

            locationRepository.save(loc);
        }
    }

    @Test
    @DisplayName("활동 정보가 알맞게 저장된다")
    void testSaveActivity() {
        activityRepository.findAll().forEach(a -> Assertions.assertEquals(LocalDate.of(2010, 1, 1), a.getActivityDate()));
    }

    @Test
    @DisplayName("구역, 날짜, 단체 필터가 적용된 활동정보 상세가 반환된다")
    void testGetLocationSpecific() {
        LocationRequest dto = LocationRequest.builder()
                .swLat(33.0)
                .swLng(124.0)
                .neLat(43.0)
                .neLng(132.0) // 전국을 포함하는 Bounding box 위경도
                .startDate(LocalDate.of(1999, 4, 5))
                .endDate(LocalDate.now())
                .build();

        long start = System.currentTimeMillis();
        List<LocationSpecificProjection> results = locationRepository.getSpecific(
                dto.getSwLat(), dto.getSwLng(),
                dto.getNeLat(), dto.getNeLng(),
                dto.getStartDate(), dto.getEndDate()
                ,null
        );
        long end = System.currentTimeMillis();

        System.out.println("\n*******************\nBounding box query result size: " + results.size());
        System.out.println("Execution time: " + (end - start) + " ms");

        Assertions.assertFalse(results.isEmpty(), "결과가 비어있으면 안 됩니다");
    }

    @Test
    @DisplayName("")
    void testGetLocationWeighted() {
        LocationWeightedRequest request = LocationWeightedRequest.builder()
                .startDate(LocalDate.of(2000, 1, 1))
                .endDate(LocalDate.now())
                .build();

        List<LocationWeightedProjection> results = locationRepository.getWeighted(request.getStartDate(), request.getEndDate());

        Assertions.assertFalse(results.isEmpty(), "결과가 비어있으면 안 됩니다");

        results.forEach(System.out::println);
    }
}
