package domain.backend.common.notification.service;

import domain.backend.auth.domain.user.User;
import domain.backend.common.notification.domain.Notification;
import domain.backend.common.notification.dto.NotificationResponse;
import domain.backend.common.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
