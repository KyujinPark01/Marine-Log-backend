package domain.backend.activity.service;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.ActivityPicture;
import domain.backend.activity.repository.ActivityPictureRepository;
import domain.backend.image.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityPictureService {
    private final ImageUploadService imageUploadService;
    private final ActivityPictureRepository activityPictureRepository;

    @Transactional
    public List<ActivityPicture> uploadPictures(Activity activity, List<MultipartFile> files) throws IOException {
        List<ActivityPicture> pictures = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = imageUploadService.uploadImage(file);
            ActivityPicture picture = ActivityPicture.builder()
                    .activity(activity)
                    .pictureUrl(url)
                    .summary("활동 사진")
                    .build();
            pictures.add(picture);
        }
        return activityPictureRepository.saveAll(pictures);
    }
}
