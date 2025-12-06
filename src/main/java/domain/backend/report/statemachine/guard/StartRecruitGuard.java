package domain.backend.report.statemachine.guard;

import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class StartRecruitGuard implements Guard<ActivityStatus, ActivityEvent> {

    /**
     * 모집 시작은 다음 중 하나일 때 가능:
     * - APPROVED (즉시 모집 시작 가능)
     * - SURVEYING 종료 후 RECRUITING 이동 가능
     */
    @Override
    public boolean evaluate(StateContext<ActivityStatus, ActivityEvent> context) {

        ActivityStatus state = context.getStateMachine().getState().getId();

        return state == ActivityStatus.SURVEYING;
    }
}
