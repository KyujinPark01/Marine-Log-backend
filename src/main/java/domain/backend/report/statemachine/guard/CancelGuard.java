package domain.backend.report.statemachine.guard;

import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class CancelGuard implements Guard<ActivityStatus, ActivityEvent> {

    /**
     * 취소는 어떤 상태에서든 허용
     * → 모든 전이 가능
     */
    @Override
    public boolean evaluate(StateContext<ActivityStatus, ActivityEvent> context) {
        return true;
    }
}
