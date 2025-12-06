package domain.backend.report.statemachine.util;

import org.springframework.statemachine.StateContext;

public class StateMachineHelper {

    public static Long getReportId(StateContext<?, ?> context) {
        return (Long) context.getMessageHeader("reportId");
    }
}
