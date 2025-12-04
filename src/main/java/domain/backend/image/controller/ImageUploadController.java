package domain.backend.image.controller;

import domain.backend.image.service.ImageUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
@Tag(name = "Image Upload API", description = "이미지 파일 업로드 및 S3 저장소 연동")

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageUploadController {
    private final ImageUploadService imageUploadService;
    @Operation(
            summary = "이미지 업로드",
            description = "단일 이미지 파일을 업로드하여 S3 저장소에 저장하고, 업로드된 파일의 URL을 반환합니다."
    )
    @PostMapping("/v1/upload/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = imageUploadService.uploadImage(file);
        return ResponseEntity
                .created(URI.create(fileUrl))
                .body(fileUrl);
    }
}
