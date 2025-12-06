package domain.backend.activity.controller;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.ActivityPicture;
import domain.backend.activity.domain.Status;
import domain.backend.activity.dto.request.ActivityCreateRequest;
import domain.backend.activity.dto.request.ActivityUpdateRequest;
import domain.backend.activity.dto.response.ActivityDetailResponse;
import domain.backend.activity.dto.response.MyActivityListResponse;
import domain.backend.activity.dto.response.PublicActivityListResponse;
import domain.backend.activity.service.ActivityPictureService;
import domain.backend.activity.service.ActivityService;

import domain.backend.common.dto.ApiResponse;
import domain.backend.common.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(
        name = "Activity API",
        description = "활동(정화 기록) 생성, 조회, 수정, 삭제 관련 API"
)
@Slf4j
@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityPictureService activityPictureService;

    // 1. 게시(바로 게시)
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "활동 게시", description = "활동을 즉시 게시 상태로 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ActivityDetailResponse>>  createAndPublish(
            @Valid @RequestBody ActivityCreateRequest dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(activityService.createAndPublish(dto, principal.userId())));
    }

    // 2. 임시저장 생성
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "활동 임시저장", description = "활동을 임시 저장 상태로 생성합니다.")
    @PostMapping("/drafts")
    public ResponseEntity<ApiResponse<ActivityDetailResponse>> createDraft(
            @RequestBody ActivityCreateRequest dto,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(activityService.createDraft(dto, principal.userId())));
    }

    // 3. 임시저장 -> 게시
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "임시저장 게시", description = "임시 저장된 활동을 게시 상태로 전환합니다.")
    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<ActivityDetailResponse>> publish(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
    return ResponseEntity.ok(ApiResponse.ok(activityService.publish(id, principal.userId())));
    }

    // 4. 게시, 임시저장 활동 수정
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "활동 수정", description = "임시저장/게시 상태와 상관없이 활동을 부분 수정합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityDetailResponse>> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityUpdateRequest dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(activityService.updateActivity(id, dto, principal.userId()))
        );
    }

    // 5. 게시/초안 -> 삭제 (소프트 삭제)
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "활동 삭제", description = "활동을 삭제 처리합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        activityService.delete(id, principal.userId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 상세 조회
    @Operation(summary = "활동 상세 조회", description = "특정 활동의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityDetailResponse>> getOne(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
            ) {
        Long userIdOrNull = principal == null ? null : principal.userId();

        log.info("📥 [ActivityController] GET /api/v1/activities/{} userUuid={}",
                id, (userIdOrNull != null ? userIdOrNull : "anonymous"));

        return ResponseEntity.ok(ApiResponse.ok(activityService.getOne(id, userIdOrNull)));
    }
    //목록 조회
    @Operation(summary = "활동 목록 조회", description = "게시된 활동 목록을 페이지 단위로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PublicActivityListResponse>>> listPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.ok(activityService.listPublished(PageRequest.of(page, size))));
    }
    //내 활동 목록 조회
    @Operation(summary = "내 활동 목록 조회", description = "로그인한 사용자의 활동(임시저장, 게시 포함)을 조회합니다.")
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<Page<MyActivityListResponse>>> listMine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<Status> status,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<Status> filter = (status == null || status.isEmpty())
                ? List.of(Status.DRAFT, Status.PUBLISHED)
                : status;
        return ResponseEntity.ok(ApiResponse.ok(activityService.listMine(principal.userId(), filter, PageRequest.of(page, size))));
    }

    @Operation(summary = "활동 사진 업로드", description = "활동에 사진 파일을 업로드하고 URL 목록을 반환합니다.")
    @PostMapping("/{id}/pictures")
    public ResponseEntity<ApiResponse<List<String>>> uploadPictures(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws IOException {
        Activity activity = activityService.getOwned(id, principal.userId());
        List<ActivityPicture> saved = activityPictureService.uploadPictures(activity, files);
        List<String> urls = saved.stream().map(ActivityPicture::getPictureUrl).toList();
        return ResponseEntity.ok(ApiResponse.ok(urls));
    }


}


