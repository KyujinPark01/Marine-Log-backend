package domain.backend.activity.repository;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.ActivityPicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityPictureRepository extends JpaRepository<ActivityPicture, Long> {
    Optional<ActivityPicture> findTopByActivityOrderByPictureIdAsc(Activity activity);
}
