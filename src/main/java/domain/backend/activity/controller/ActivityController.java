package domain.backend.activity.controller;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.ActivityPicture;
import domain.backend.activity.domain.Status;
import domain.backend.activity.dto.request.ActivityCreateRequest;
import domain.backend.activity.dto.request.ActivityUpdateRequest;
import domain.backend.activity.dto.response.ActivityResponse;
import domain.backend.activity.service.ActivityPictureService;
import domain.backend.activity.service.ActivityService;
import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityPictureService activityPictureService;

    // 1. 게시(바로 게시)
    @Operation(summary = "활동 게시", description = "활동을 즉시 게시 상태로 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ActivityResponse>>  createAndPublish(
            @Valid @RequestBody ActivityCreateRequest dto) {
        User me = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(activityService.createAndPublish(dto, me)));
    }

    // 2. 임시저장 생성
    @Operation(summary = "활동 임시저장", description = "활동을 임시 저장 상태로 생성합니다.")
    @PostMapping("/drafts")
    public ResponseEntity<ApiResponse<ActivityResponse>> createDraft(
            @RequestBody ActivityCreateRequest dto) {
        User me = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(activityService.createDraft(dto, me)));
    }

    // 3. 임시저장 -> 게시
    @Operation(summary = "임시저장 게시", description = "임시 저장된 활동을 게시 상태로 전환합니다.")
    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<ActivityResponse>> publish(@PathVariable Long id) {
        User me = getCurrentUser();
    return ResponseEntity.ok(ApiResponse.ok(activityService.publish(id, me.getUserId())));
    }

    // 4. 게시 -> 수정 (부분수정)
    @Operation(summary = "활동 수정", description = "게시된 활동 정보를 부분 수정합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityResponse>> updatePublished(
            @PathVariable Long id,
            @Valid @RequestBody ActivityUpdateRequest dto) {
        User me = getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok(activityService.updatePublished(id, dto, me.getUserId())));
    }

    // 5. 게시/초안 -> 삭제 (소프트 삭제)
    @Operation(summary = "활동 삭제", description = "활동을 삭제 처리합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        User me = getCurrentUser();
        activityService.delete(id, me.getUserId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 상세 조회
    @Operation(summary = "활동 상세 조회", description = "특정 활동의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityResponse>> getOne(@PathVariable Long id) {
        User me = getCurrentUserOrNull();
        log.info("📥 [ActivityController] GET /api/v1/activities/{} userId={}", id,
                (me != null ? me.getUserId() : "anonymous"));

        return ResponseEntity.ok(ApiResponse.ok(activityService.getOne(id, me != null ? me.getUserId() : null)));
    }
    //목록 조회
    @Operation(summary = "활동 목록 조회", description = "게시된 활동 목록을 페이지 단위로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ActivityResponse>>> listPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(activityService.listPublished(PageRequest.of(page, size))));
    }
    //내 활동 목록 조회
    @Operation(summary = "내 활동 목록 조회", description = "로그인한 사용자의 활동(임시저장, 게시 포함)을 조회합니다.")
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<Page<ActivityResponse>>> listMine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<Status> status // 예: ?status=DRAFT&status=PUBLISHED
    ) {
        User me = getCurrentUser();
        List<Status> filter = (status == null || status.isEmpty())
                ? List.of(Status.DRAFT, Status.PUBLISHED, Status.DELETED)
                : status;
        return ResponseEntity.ok(ApiResponse.ok(activityService.listMine(me, filter, PageRequest.of(page, size))));
    }
    @Operation(summary = "활동 사진 업로드", description = "활동에 사진 파일을 업로드하고 URL 목록을 반환합니다.")
    @PostMapping("/{id}/pictures")
    public ResponseEntity<ApiResponse<List<String>>> uploadPictures(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {
        User me = getCurrentUser();
        Activity activity = activityService.getOwned(id, me.getUserId());
        List<ActivityPicture> saved = activityPictureService.uploadPictures(activity, files);
        List<String> urls = saved.stream().map(ActivityPicture::getPictureUrl).toList();
        return ResponseEntity.ok(ApiResponse.ok(urls));
    }

    /*[테스트 전용] Security 연동 후 대체 */
    //로그인된 사용자 시뮬레이션
    // ToDo: 현재 실험적으로 구현한 상태 (1L은 최초 등록한 사람만 로그인하여 활동을 기록할 수 있게 함)
    private User getCurrentUser() {
        return new User(
                1L,
                "test@email.com",
                "테스트 유저",
                "서울시 어딘가",
                "010-1234-5678",
                Role.USER,
                null,
                null,
                null
        );
    }

    // 비회원 일 때
    private User getCurrentUserOrNull() {
        boolean guestMode = false; // true면 비회원 모드
        return guestMode ? null : getCurrentUser();
    }


}


