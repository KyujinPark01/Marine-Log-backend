package domain.backend.common.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final S3Client s3Client;
    

}
