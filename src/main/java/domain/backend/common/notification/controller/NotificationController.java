package domain.backend.common.notification.controller;

import domain.backend.auth.domain.user.User;
import domain.backend.common.notification.dto.NotificationResponse;
import domain.backend.common.notification.service.NotificationService;
import domain.backend.common.notification.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SseEmitterService sseEmitterService;
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe() {
        // ToDo: Get user from security context
        User me = getCurrentUser();
        return ResponseEntity.ok(sseEmitterService.subscribe(me.getUserId()));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User me = getCurrentUser();
        return ResponseEntity.ok(notificationService.getNotifications(me, PageRequest.of(page, size)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount() {
        User me = getCurrentUser();
        return ResponseEntity.ok(notificationService.countUnread(me));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long notificationId) {
        User me = getCurrentUser();
        return ResponseEntity.ok(notificationService.markAsRead(notificationId, me));
    }

    /*[테스트 전용] Security 연동 후 대체 */
    private User getCurrentUser() {
        return new User(
                1L,
                "user1@example.com",
                "User 1",
                "Ulleung, Korea",
                "010-7079-6868",
                domain.backend.auth.domain.user.Role.USER,
                null,
                null,
                null
        );
    }
}
