package domain.backend.report.statemachine.guard;

import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class CollaborationRequestGuard implements Guard<ActivityStatus, ActivityEvent> {

    /**
     * 연합 활동 요청은 다음 상태에서만 가능:
     * - SURVEYING
     * - RECRUITING
     */
    @Override
    public boolean evaluate(StateContext<ActivityStatus, ActivityEvent> context) {

        ActivityStatus state = context.getStateMachine().getState().getId();

        return (state == ActivityStatus.SURVEYING || state == ActivityStatus.RECRUITING);
    }
}
