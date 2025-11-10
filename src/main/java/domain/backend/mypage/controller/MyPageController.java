package domain.backend.mypage.controller;

import domain.backend.auth.domain.user.User;
import domain.backend.common.notification.dto.NotificationResponse;
import domain.backend.common.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // ToDo: Get user from security context
        User me = getCurrentUser();
        return ResponseEntity.ok(notificationService.getNotifications(me, PageRequest.of(page, size)));
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
