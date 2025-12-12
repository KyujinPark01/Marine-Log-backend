package domain.backend.report.statemachine.config;

import domain.backend.report.statemachine.action.*;
import domain.backend.report.statemachine.guard.*;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class ActivityStateMachineConfig
        extends StateMachineConfigurerAdapter<ActivityStatus, ActivityEvent> {

    // ===== ACTIONS =====
    private final ApproveAction approveAction;
    private final RejectAction rejectAction;
    private final StartSurveyAction startSurveyAction;
    private final FinishSurveyAction finishSurveyAction;
    private final StartRecruitAction startRecruitAction;
    private final StartActivityAction startActivityAction;
    private final FinishActivityAction finishActivityAction;
    private final CancelAction cancelAction;
    private final RecruitingCreateActivityAction recruitingCreateActivityAction;

    // Collaboration Actions
    private final CollaborationRequestAction collaborationRequestAction;
    private final CollaborationApproveAction collaborationApproveAction;
    private final CollaborationRejectAction collaborationRejectAction;

    // ===== GUARDS =====
    private final ApproveGuard approveGuard;
    private final StartSurveyGuard startSurveyGuard;
    private final FinishSurveyGuard finishSurveyGuard;
    private final StartRecruitGuard startRecruitGuard;

    // Collaboration Guards
    private final CollaborationRequestGuard collaborationRequestGuard;
    private final CollaborationApproveGuard collaborationApproveGuard;
    private final CollaborationRejectGuard collaborationRejectGuard;


    @Override
    public void configure(StateMachineStateConfigurer<ActivityStatus, ActivityEvent> states)
            throws Exception {
        states
                .withStates()
                .initial(ActivityStatus.REPORTED)
                .states(EnumSet.allOf(ActivityStatus.class));
    }


    @Override
    public void configure(StateMachineTransitionConfigurer<ActivityStatus, ActivityEvent> t)
            throws Exception {

        t
                // ===== 제보 승인 =====
                .withExternal()
                .source(ActivityStatus.REPORTED)
                .target(ActivityStatus.APPROVED)
                .event(ActivityEvent.APPROVE)
                .guard(approveGuard)
                .action(approveAction)

                // ===== 반려 =====
                .and().withExternal()
                .source(ActivityStatus.REPORTED)
                .target(ActivityStatus.REJECTED)
                .event(ActivityEvent.REJECT)
                .action(rejectAction)

                // ===== 답사 시작 =====
                .and().withExternal()
                .source(ActivityStatus.APPROVED)
                .target(ActivityStatus.SURVEYING)
                .event(ActivityEvent.START_SURVEY)
                .guard(startSurveyGuard)
                .action(startSurveyAction)

                // ===== 답사 완료 → 모집 =====
                .and().withExternal()
                .source(ActivityStatus.SURVEYING)
                .target(ActivityStatus.RECRUITING)
                .event(ActivityEvent.FINISH_SURVEY)
                .guard(finishSurveyGuard)
                .action(finishSurveyAction)



                // ===== 모집 → 진행 =====
                .and().withExternal()
                .source(ActivityStatus.RECRUITING)
                .target(ActivityStatus.IN_PROGRESS)
                .event(ActivityEvent.START_ACTIVITY)
                .action(startActivityAction)

                // ===== 활동 → 완료 =====
                .and().withExternal()
                .source(ActivityStatus.IN_PROGRESS)
                .target(ActivityStatus.DONE)
                .event(ActivityEvent.FINISH_ACTIVITY)
                .action(finishActivityAction)

                // ======================================================
                // 🔥 연합 활동 관련 전이
                // ======================================================

                // 답사 중 연합 요청
                .and().withExternal()
                .source(ActivityStatus.SURVEYING)
                .target(ActivityStatus.SURVEYING)
                .event(ActivityEvent.COLLAB_REQUEST)
                .guard(collaborationRequestGuard)
                .action(collaborationRequestAction)

                // 연합 승인
                .and().withExternal()
                .source(ActivityStatus.SURVEYING)
                .target(ActivityStatus.SURVEYING)
                .event(ActivityEvent.COLLAB_APPROVE)
                .guard(collaborationApproveGuard)
                .action(collaborationApproveAction)

                // 연합 거절
                .and().withExternal()
                .source(ActivityStatus.SURVEYING)
                .target(ActivityStatus.SURVEYING)
                .event(ActivityEvent.COLLAB_REJECT)
                .guard(collaborationRejectGuard)
                .action(collaborationRejectAction)


                // ===== 취소 (모든 상태 가능) =====
                .and().withExternal()
                .source(ActivityStatus.REPORTED)
                .target(ActivityStatus.CANCELED)
                .event(ActivityEvent.CANCEL)
                .action(cancelAction)

                .and().withExternal()
                .source(ActivityStatus.APPROVED)
                .target(ActivityStatus.CANCELED)
                .event(ActivityEvent.CANCEL)
                .action(cancelAction)

                .and().withExternal()
                .source(ActivityStatus.SURVEYING)
                .target(ActivityStatus.CANCELED)
                .event(ActivityEvent.CANCEL)
                .action(cancelAction)

                .and().withExternal()
                .source(ActivityStatus.RECRUITING)
                .target(ActivityStatus.CANCELED)
                .event(ActivityEvent.CANCEL)
                .action(cancelAction)

                .and().withExternal()
                .source(ActivityStatus.IN_PROGRESS)
                .target(ActivityStatus.CANCELED)
                .event(ActivityEvent.CANCEL)
                .action(cancelAction);
    }
}
