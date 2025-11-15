package domain.backend.notification.service;

import domain.backend.auth.domain.user.User;
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

    public Page<NotificationResponse> getNotifications(User user, Pageable pageable) {
        return notificationRepository.findByTargetUserOrderByCreatedAtDesc(user, pageable)
                .map(NotificationResponse::from);
    }

    public long countUnread(User user) {
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

        sseEmitterService.sendNotification(target.getUserId(), "notification", NotificationResponse.from(notification));
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + notificationId));
        if (!notification.getTargetUser().getUserId().equals(user.getUserId())) {
            try {
                throw new AccessDeniedException("읽기 권한이 없습니다.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }
        if (!notification.isRead()) {
            notification.markAsRead();
        }
        return NotificationResponse.from(notification);
    }
}
