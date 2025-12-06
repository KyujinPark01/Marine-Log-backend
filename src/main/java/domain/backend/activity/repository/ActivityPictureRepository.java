package domain.backend.activity.repository;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.ActivityPicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityPictureRepository extends JpaRepository<ActivityPicture, Long> {
    //대표 사진 1장만 조회
    Optional<ActivityPicture> findTopByActivityOrderByPictureIdAsc(Activity activity);
    //활동의 모든 사진 조회(상세 조회, 활도 수정 시)
   List<ActivityPicture> findByActivityOrderByPictureIdAsc(Activity activity);
}
