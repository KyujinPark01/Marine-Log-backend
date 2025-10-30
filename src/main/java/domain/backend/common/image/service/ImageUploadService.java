package domain.backend.common.image.service;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private final String bucketName = "";
    private final String regionName = s3Client.serviceClientConfiguration().region().id();
    
    public String uploadImage(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        // WebP로 변환 & 압축
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .size(originalImage.getWidth(), originalImage.getHeight())
                .outputFormat("webp") // WebP 포맷
                .outputQuality(0.8)  // 압축 품질 0.0 ~ 1.0
                .toOutputStream(os);
        byte[] webpBytes = os.toByteArray();

        // UUID로 파일 이름 생성
        String fileName = "uploads/" + UUID.randomUUID() + ".webp";

        // S3 업로드
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType("image/webp")
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(webpBytes)
        );

        // 업로드된 파일 URL 반환
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, regionName, fileName);
    }
}
