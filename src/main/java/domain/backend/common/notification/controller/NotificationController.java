package domain.backend.common.notification.controller;

import domain.backend.auth.domain.user.User;
import domain.backend.common.notification.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SseEmitterService sseEmitterService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe() {
        // ToDo: Get user from security context
        User me = getCurrentUser();
        return ResponseEntity.ok(sseEmitterService.subscribe(me.getUserId()));
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
