package domain.backend.report.statemachine.controller;

import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import domain.backend.common.security.auth.UserPrincipal;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.dto.response.CollaborationResponse;
import domain.backend.report.statemachine.service.ActivityStateMachineService;

import domain.backend.report.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activity-report")
public class ActiveReportController {

    private final UserRepository userRepository;
    private final ActivityStateMachineService stateMachineService;
    private final CollaborationService collaborationService;
    private User getActor(UserPrincipal principal) {
        return userRepository.findById(principal.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // 승인
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) throws Exception {

        stateMachineService.sendEvent(id, ActivityEvent.APPROVE, getActor(principal));
        return ResponseEntity.ok().build();
    }

    // 반려
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) throws Exception {

        stateMachineService.sendEvent(id, ActivityEvent.REJECT, getActor(principal));
        return ResponseEntity.ok().build();
    }

    // 답사 시작
    @PatchMapping("/{id}/start-survey")
    public ResponseEntity<Void> startSurvey(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) throws Exception {

        stateMachineService.sendEvent(id, ActivityEvent.START_SURVEY, getActor(principal));
        return ResponseEntity.ok().build();
    }

    // 답사 완료 → 모집 시작
    @PatchMapping("/{id}/finish-survey")
    public ResponseEntity<Void> finishSurvey(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) throws Exception {

        stateMachineService.sendEvent(id, ActivityEvent.FINISH_SURVEY, getActor(principal));
        return ResponseEntity.ok().build();
    }



    // 활동 시작
    @PatchMapping("/{id}/start")
    public ResponseEntity<Void> startActivity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) throws Exception {

        stateMachineService.sendEvent(id, ActivityEvent.START_ACTIVITY, getActor(principal));
        return ResponseEntity.ok().build();
    }

    // 활동 완료
    @PatchMapping("/{id}/finish")
    public ResponseEntity<Void> finishActivity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) throws Exception {

        stateMachineService.sendEvent(id, ActivityEvent.FINISH_ACTIVITY, getActor(principal));
        return ResponseEntity.ok().build();
    }

    // 취소
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) throws Exception {

        stateMachineService.sendEvent(id, ActivityEvent.CANCEL, getActor(principal));
        return ResponseEntity.ok().build();
    }

    //연합 기능
    // 연합 요청 목록 조회
    @GetMapping("/{id}/collaboration")
    public ResponseEntity<List<CollaborationResponse>> getCollaborations(
            @PathVariable Long id) {

        List<CollaborationResponse> list = collaborationService.getByReport(id);
        return ResponseEntity.ok(list);
    }
    // 연합 요청
    @PostMapping("/{id}/collaboration")
    public ResponseEntity<Void> requestCollab(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestParam Long partnerOrgId
    ) throws Exception {

        User actor = getActor(principal);

        stateMachineService.sendEvent(
                id,
                ActivityEvent.COLLAB_REQUEST,
                actor,
                headers -> headers.setHeader("partnerOrgId", partnerOrgId)
        );

        return ResponseEntity.ok().build();
    }


    // 연합 승인
    @PatchMapping("/{id}/collaboration/approve")
    public ResponseEntity<Void> approveCollab(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestParam Long partnerOrgId
    ) throws Exception {

        User actor = getActor(principal);

        stateMachineService.sendEvent(
                id,
                ActivityEvent.COLLAB_APPROVE,
                actor,
                headers -> headers.setHeader("partnerOrgId", partnerOrgId)
        );

        return ResponseEntity.ok().build();
    }

    // 연합 거절
    @PatchMapping("/{id}/collaboration/reject")
    public ResponseEntity<Void> rejectCollab(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestParam Long partnerOrgId
    ) throws Exception {

        User actor = getActor(principal);

        stateMachineService.sendEvent(
                id,
                ActivityEvent.COLLAB_REJECT,
                actor,
                headers -> headers.setHeader("partnerOrgId", partnerOrgId)
        );

        return ResponseEntity.ok().build();
    }

}
