package domain.backend;


import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.ActivityRepository;
import domain.backend.activity.domain.Status;
import domain.backend.location.domain.Location;
import domain.backend.location.domain.LocationRepository;
import domain.backend.location.dto.LocationRequestDto;
import domain.backend.location.projection.LocationSpecificProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    void testBoundingBoxQueryPerformance() {
        LocationRequestDto dto = new LocationRequestDto(37.5, 127.0, 37.6, 127.1);

        long start = System.currentTimeMillis();
        List<LocationSpecificProjection> results = locationRepository.getSpecific(
                dto.getSwLat(), dto.getSwLng(),
                dto.getNeLat(), dto.getNeLng()
        );
        long end = System.currentTimeMillis();

        System.out.println("Bounding box query result size: " + results.size());
        System.out.println("Execution time: " + (end - start) + " ms");

        Assertions.assertFalse(results.isEmpty(), "결과가 비어있으면 안 됩니다");
    }

    @Test
    void testSeqScanVsIndexScan() {
        LocationRequestDto dto = new LocationRequestDto(37.5, 127.0, 37.6, 127.1);

        // 참고: 'SET enable_seqscan'은 PostgreSQL 전용 쿼리입니다.
        // 다른 DB(H2 등)에서는 테스트가 실패할 수 있습니다.
        em.flush(); // BeforeEach의 setup 변경사항을 DB에 반영

        // Seq Scan 강제
        em.createNativeQuery("SET enable_seqscan = ON").executeUpdate();
        long startSeq = System.currentTimeMillis();
        List<LocationSpecificProjection> seqResults = locationRepository.getSpecific(dto.getSwLat(), dto.getSwLng(), dto.getNeLat(), dto.getNeLng());
        long endSeq = System.currentTimeMillis();

        // GiST 인덱스 강제
        em.createNativeQuery("SET enable_seqscan = OFF").executeUpdate();
        long startIndex = System.currentTimeMillis();
        List<LocationSpecificProjection> indexResults = locationRepository.getSpecific(dto.getSwLat(), dto.getSwLng(), dto.getNeLat(), dto.getNeLng());
        long endIndex = System.currentTimeMillis();

        System.out.println("Seq Scan time: " + (endSeq - startSeq) + " ms");
        System.out.println("GiST Index Scan time: " + (endIndex - startIndex) + " ms");

        Assertions.assertFalse(seqResults.isEmpty(), "순차 스캔 결과가 비어있으면 안 됩니다.");
        Assertions.assertFalse(indexResults.isEmpty(), "인덱스 스캔 결과가 비어있으면 안 됩니다.");
        Assertions.assertEquals(seqResults.size(), indexResults.size(), "두 쿼리의 결과 개수가 동일해야 합니다.");
    }
}
