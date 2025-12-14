package domain.backend.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseEmitterService {

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long CONNECTION_TIME = 3600000L;

    public SseEmitter subscribe(UUID uuid) {
        SseEmitter emitter = new SseEmitter(CONNECTION_TIME); // 1 hour
        emitters.put(uuid, emitter);

        emitter.onCompletion(() -> {
            log.info("SSE completed for user {}", uuid);
            emitters.remove(uuid);
        });
        emitter.onTimeout(() -> {
            log.info("SSE timed out for user {}", uuid);
            emitters.remove(uuid);
        });
        emitter.onError(e -> {
            log.error("SSE error for user {}: {}", uuid, e.getMessage());
            emitters.remove(uuid);
        });

        return emitter;
    }

    public void sendNotification(UUID uuid, String eventName, Object data) {
        SseEmitter emitter = emitters.get(uuid);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                log.error("Failed to send SSE event to user {}: {}", uuid, e.getMessage());
                emitters.remove(uuid);
            }
        }
    }
}
