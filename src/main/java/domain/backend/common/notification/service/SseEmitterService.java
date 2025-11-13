package domain.backend.common.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseEmitterService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(3600000L); // 1 hour
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            log.info("SSE completed for user {}", userId);
            emitters.remove(userId);
        });
        emitter.onTimeout(() -> {
            log.info("SSE timed out for user {}", userId);
            emitters.remove(userId);
        });
        emitter.onError(e -> {
            log.error("SSE error for user {}: {}", userId, e.getMessage());
            emitters.remove(userId);
        });

        // ToDo: test notification for sure (later)
        // Send a dummy event to establish the connection
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            log.error("Failed to send initial SSE event for user {}: {}", userId, e.getMessage());
            emitters.remove(userId);
        }

        return emitter;
    }

    public void sendNotification(Long userId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                log.error("Failed to send SSE event to user {}: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        }
    }
}
