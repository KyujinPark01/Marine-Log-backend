package domain.backend.activity.controller;

import domain.backend.activity.domain.Status;
import domain.backend.activity.dto.request.ActivityCreateRequest;
import domain.backend.activity.dto.request.ActivityUpdateRequest;
import domain.backend.activity.dto.response.ActivityResponse;
import domain.backend.activity.service.ActivityService;
import domain.backend.auth.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    // 1. 게시(바로 게시)
    @PostMapping
    public ResponseEntity<ActivityResponse> createAndPublish(
            @RequestBody ActivityCreateRequest dto) {
        User me = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityService.createAndPublish(dto, me));
    }

    // 2. 임시저장 생성
    @PostMapping("/drafts")
    public ResponseEntity<ActivityResponse> createDraft(
            @RequestBody ActivityCreateRequest dto) {
        User me = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityService.createDraft(dto, me));
    }

    // 3. 임시저장 -> 게시
    @PostMapping("/{id}/publish")
    public ResponseEntity<ActivityResponse> publish(@PathVariable Long id) {
        User me = getCurrentUser();
        return ResponseEntity.ok(activityService.publish(id, me.getUserId()));
    }

    // 4. 게시 -> 수정 (부분수정)
    @PatchMapping("/{id}")
    public ResponseEntity<ActivityResponse> updatePublished(
            @PathVariable Long id,
            @RequestBody ActivityUpdateRequest dto) {
        User me = getCurrentUser();
        return ResponseEntity.ok(activityService.updatePublished(id, dto, me.getUserId()));
    }

    // 5. 게시/초안 -> 삭제 (소프트 삭제)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User me = getCurrentUser();
        activityService.delete(id, me.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 조회
    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> getOne(@PathVariable Long id) {
        User me = getCurrentUserOrNull();
        return ResponseEntity.ok(activityService.getOne(id, me != null ? me.getUserId() : null));
    }

    @GetMapping
    public ResponseEntity<Page<ActivityResponse>> listPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(activityService.listPublished(PageRequest.of(page, size)));
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<ActivityResponse>> listMine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<Status> status // 예: ?status=DRAFT&status=PUBLISHED
    ) {
        User me = getCurrentUser();
        List<Status> filter = (status == null || status.isEmpty())
                ? List.of(Status.DRAFT, Status.PUBLISHED, Status.DELETED)
                : status;
        return ResponseEntity.ok(activityService.listMine(me, filter, PageRequest.of(page, size)));
    }
    /* Security 연동 후 대체 */
    private User getCurrentUser() { return User.builder().userId(1L).name("테스트 유저").build(); }
    private User getCurrentUserOrNull() { return getCurrentUser(); } // 비로그인 허용이면 null 리턴

}

