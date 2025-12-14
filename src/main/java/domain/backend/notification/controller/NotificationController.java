package domain.backend.notification.controller;

import domain.backend.common.security.auth.UserPrincipal;
import domain.backend.notification.dto.NotificationResponse;
import domain.backend.notification.service.NotificationService;
import domain.backend.notification.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final SseEmitterService sseEmitterService;
    private final NotificationService notificationService;

    @GetMapping(value = "/v1/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(sseEmitterService.subscribe(principal.uuid()));
    }

    @GetMapping("/v1/notifications")
    public ResponseEntity<Page<NotificationResponse>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                notificationService.getNotifications(principal.userId(), PageRequest.of(page, size))
        );
    }

    @GetMapping("/v1/notifications/unread")
    public ResponseEntity<Long> unreadCount(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.countUnread(principal.userId()));
    }

    @PatchMapping("/v1/notifications/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long notificationId) {

        return ResponseEntity.ok(notificationService.markAsRead(notificationId, principal.userId()));
    }

    @GetMapping("/v1/notifications/check")
    public ResponseEntity<?> test(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.test(principal);

        return ResponseEntity.ok(null);
    }


}
