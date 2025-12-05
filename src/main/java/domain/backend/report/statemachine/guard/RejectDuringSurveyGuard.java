package domain.backend.report.statemachine.guard;

import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class RejectDuringSurveyGuard implements Guard<ActivityStatus, ActivityEvent> {

    @Override
    public boolean evaluate(StateContext<ActivityStatus, ActivityEvent> context) {

        ActivityStatus state = context.getStateMachine().getState().getId();
        User actor = (User) context.getMessageHeader("user");

        // SURVEYING 상태일 때만 가능
        if (state != ActivityStatus.SURVEYING) return false;

        // HOST 권한만 가능
        return actor.getRole() == Role.HOST;
    }
}
