package domain.backend.report.statemachine.service;

import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ActivityStateMachineService {

    private final StateMachineFactory<ActivityStatus, ActivityEvent> factory;
    private final StateMachinePersist<ActivityStatus, ActivityEvent, Long> persist;

    /**
     * 기본 이벤트 전송
     */
    public void sendEvent(Long reportId, ActivityEvent event, Object user) throws Exception {
        sendEvent(reportId, event, user, null);
    }

    /**
     * header 커스터마이징 지원
     */
    public void sendEvent(
            Long reportId,
            ActivityEvent event,
            Object user,
            Consumer<MessageBuilder<ActivityEvent>> headerCustomizer
    ) throws Exception {

        // 1) 상태머신 가져오기
        StateMachine<ActivityStatus, ActivityEvent> sm = factory.getStateMachine(reportId.toString());
        sm.stop();

        // 2) DB의 기존 상태 불러오기
        StateMachineContext<ActivityStatus, ActivityEvent> context = persist.read(reportId);

        sm.getStateMachineAccessor().doWithAllRegions(access ->
                access.resetStateMachine(context)
        );

        sm.start();

        // 3) Message 생성
        MessageBuilder<ActivityEvent> builder = MessageBuilder
                .withPayload(event)
                .setHeader("reportId", reportId)
                .setHeader("user", user);

        if (headerCustomizer != null) {
            headerCustomizer.accept(builder);
        }

        Message<ActivityEvent> message = builder.build();

        // 4) 🔥 이벤트를 동기 방식으로 전송 (반드시 이것 사용!)
        boolean accepted = sm.sendEvent(message);

        if (!accepted) {
            System.out.println("[WARN] Event rejected: " + event + " (reportId=" + reportId + ")");
        }

        // 5) 🔥 전이 후 상태를 DB에 저장
        ActivityStatus newState = sm.getState().getId();

        persist.write(
                new DefaultStateMachineContext<>(
                        newState,
                        null,
                        null,
                        null
                ),
                reportId
        );
    }
}
