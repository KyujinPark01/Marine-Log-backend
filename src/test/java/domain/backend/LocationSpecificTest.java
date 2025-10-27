package domain.backend;


import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.Status;
import domain.backend.location.domain.Location;
import domain.backend.location.domain.LocationRepository;
import domain.backend.location.dto.LocationRequestDto;
import domain.backend.location.projection.LocationSpecificProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager em;

    private final Random random = new Random();

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        int numEntities = 10; // 생성할 Activity + Location 수

        for (int i = 0; i < numEntities; i++) {
            // Activity 생성
            Activity act = new Activity();
            act.setName("Activity " + i);
            act.setDescription("Description " + i);
            act.setMaxParticipants(50);
            act.setCurrentParticipants(random.nextInt(50));
            act.setStatus(Status.PLANNED);
            act.setStartDatetime(LocalDate.of(2010, 1, 1));
            activityRepository.save(act);

            // Location 생성 (1:1)
            double lat = 35 + random.nextDouble() * 3; // 예: 35~38
            double lng = 126 + random.nextDouble() * 3; // 예: 126~129

            Location loc = new Location();
            loc.setActivity(act);
            loc.setGeometry(new org.locationtech.jts.geom.GeometryFactory().createPoint(new Coordinate(lng, lat)));
            loc.setRegion1depthName("Region" + (i % 10));
            loc.setRegion2depthName("SubRegion" + (i % 20));
            loc.setRegion3depthName("Detail" + (i % 50));
            loc.setAddress("Address " + i);


            locationRepository.save(loc);
        }
    }

    @Test
    @DisplayName("활동 정보가 알맞게 저장된다")
    void testSaveActivity() {
        activityRepository.findAll().forEach(a -> Assertions.assertEquals(LocalDate.of(2010, 1, 1), a.getStartDatetime()));
    }

    @Test
    @DisplayName("구역, 날짜, 단체 필터가 적용된 활동정보 상세가 반환된다")
    void testGetLocationSpecific() {
        LocationRequestDto dto = LocationRequestDto.builder()
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
}
