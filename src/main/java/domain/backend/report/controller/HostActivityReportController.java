package domain.backend.report.controller;


import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.report.dto.request.ActivityReportStatusUpdateRequest;
import domain.backend.report.dto.response.ActivityReportResponse;
import domain.backend.report.service.HandleActivityReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * 단체장 권한을 위한 기능 구현
 * (단체장 권한을 확인 후) 아래와 같은 리스트를 처리:
 * 1. 타 단체의 연합 활동 제안 처리
 * 2. 활동 제보 접수 처리
 */

@RestController
@RequestMapping("/activity-report")
@RequiredArgsConstructor
public class HostActivityReportController {

    private final HandleActivityReportService handleActivityReportService;

    public boolean checkHostRole(User user){
        return user.getRole() != Role.HOST;
    }

    // ToDo: refactor theses codes, they have repeating pattern
    @PostMapping("/{activityReportId}/suggest-union")
    public ResponseEntity<Void> suggestUnionActivity(@PathVariable Long activityReportId) {

        User me = getCurrentUser();

        // 1. check the user's role
        if (checkHostRole(me)) {
            // 1-1. authorized for those with HOST role
            handleActivityReportService.suggestUnionActivity(activityReportId, me);
            return ResponseEntity.ok().build();
        }
        // 1-2. unauthorized for those without HOST role
        else  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PatchMapping("/{activityReportId}/")
    public ResponseEntity<ActivityReportResponse> acceptActivityReport(@PathVariable Long activityReportId,
                                                               @RequestBody ActivityReportStatusUpdateRequest request) {

        User me = getCurrentUser();

        // 1. check the user's role
        if (checkHostRole(me)) {
            // 1-1. authorized for those with HOST role
            handleActivityReportService.updateStatus(activityReportId, request);
            return ResponseEntity.ok(handleActivityReportService.updateStatus(activityReportId, request));
        }
        // 1-2. unauthorized for those without HOST role
        else  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    // ToDo: replace it with Spring security & actual user
    /*[테스트 전용] Security 연동 후 대체 */
    private User getCurrentUser() {
        return new User(
                2L, // Assuming user 2 is sending the suggestion
                "user2@example.com",
                "User 2",
                "Busan, Korea",
                "010-2490-6535",
                domain.backend.auth.domain.user.Role.USER,
                null,
                null,
                null
        );
    }
}
