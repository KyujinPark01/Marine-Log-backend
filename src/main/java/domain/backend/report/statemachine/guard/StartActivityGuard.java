package domain.backend.report.statemachine.guard;

import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class StartActivityGuard implements Guard<ActivityStatus, ActivityEvent> {

    @Override
    public boolean evaluate(StateContext<ActivityStatus, ActivityEvent> context) {

        ActivityStatus current = context.getStateMachine().getState().getId();

        return current == ActivityStatus.RECRUITING;
    }
}
