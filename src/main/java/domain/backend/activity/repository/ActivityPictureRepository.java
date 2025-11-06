package domain.backend.activity.repository;

import domain.backend.activity.domain.ActivityPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityPictureRepository extends JpaRepository<ActivityPicture, Long> {
    @Query(value = """
            SELECT p.picture_url 
            FROM activity_picture p 
            WHERE p.activity_id = :activityId 
            ORDER BY p.picture_id ASC 
            LIMIT 1
            """, nativeQuery = true)
    String findFirstPictureUrlByActivityId(@Param("activityId") Long activityId);
}
