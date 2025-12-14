package domain.backend.notification.service;

import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import domain.backend.common.security.auth.UserPrincipal;
import domain.backend.notification.domain.Notification;
import domain.backend.notification.dto.NotificationResponse;
import domain.backend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;
    private final UserRepository userRepository;

    public Page<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return notificationRepository.findByTargetUserOrderByCreatedAtDesc(user, pageable)
                .map(NotificationResponse::from);
    }

    public long countUnread(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return notificationRepository.countByTargetUserAndIsReadFalse(user);
    }

    @Transactional
    public void send(User target, User sender, String message, String type) {
        Notification notification = Notification.builder()
                .targetUser(target)
                .sentUser(sender)
                .message(message)
                .type(type)
                .build();

        notificationRepository.save(notification);

        sseEmitterService.sendNotification(
                target.getUuid(),
                "notification",
                NotificationResponse.from(notification)
        );
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        if (!notification.getTargetUser().getUserId().equals(me.getUserId())) {
            throw new RuntimeException("읽기 권한이 없습니다.");
        }

        if (!notification.isRead()) {
            notification.markAsRead();
        }

        return NotificationResponse.from(notification);
    }

    @Transactional
    public void test(UserPrincipal principal) {
        User user = userRepository.findById(principal.userId()).orElseThrow(IllegalArgumentException::new);

        send(user, user, "Test", "test");
    }
}
