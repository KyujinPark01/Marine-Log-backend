package domain.backend.report.statemachine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import domain.backend.common.security.auth.UserPrincipal;
import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.repository.ActivityReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ActiveReportControllerFlowTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private UserRepository userRepository;
    @Autowired private ActivityReportRepository reportRepository;

    private Long reportId;
    private User actor;

    @BeforeEach
    void setup() {

        actor = userRepository.save(
                User.builder()
                        .email("test+" + UUID.randomUUID() + "@mail.com")
                        .name("테스트유저")
                        .role(Role.HOST)
                        .build()
        );

        // SecurityContext 설정
        UserPrincipal principal = new UserPrincipal(
                actor.getUuid(),
                actor.getEmail(),
                actor.getName(),
                actor.getRole().name(),
                actor.getUserId()
        );
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        // REPORTED 상태 ActivityReport 생성
        ActivityReport report = ActivityReport.builder()
                .reporter(actor)
                .title("테스트 신고")
                .reportedReason("테스트 이유")
                .reporterOccupation("테스터")
                .status(ActivityStatus.REPORTED)
                .photoUrl("test.jpg")
                .latitude(37.123)
                .longitude(127.123)
                .build();

        reportId = reportRepository.save(report).getActivityReportId();
    }


    private void assertStatus(ActivityStatus expected) {
        ActivityReport report = reportRepository.findById(reportId).orElseThrow();
        assertThat(report.getStatus()).isEqualTo(expected);
        System.out.println("✔ 상태 검증 성공: " + expected);
    }

    @Test
    @DisplayName("전체 흐름: 승인 → 답사 시작 → 답사 완료 → 모집 → 활동 시작 → 활동 완료")
    void fullFlow() throws Exception {

        assertStatus(ActivityStatus.REPORTED);

        mockMvc.perform(patch("/activity-report/{id}/approve", reportId))
                .andExpect(status().isOk());
        assertStatus(ActivityStatus.APPROVED);

        mockMvc.perform(patch("/activity-report/{id}/start-survey", reportId))
                .andExpect(status().isOk());
        assertStatus(ActivityStatus.SURVEYING);

        mockMvc.perform(patch("/activity-report/{id}/finish-survey", reportId))
                .andExpect(status().isOk());
        assertStatus(ActivityStatus.RECRUITING);

        mockMvc.perform(patch("/activity-report/{id}/start", reportId))
                .andExpect(status().isOk());
        assertStatus(ActivityStatus.IN_PROGRESS);

        mockMvc.perform(patch("/activity-report/{id}/finish", reportId))
                .andExpect(status().isOk());
        assertStatus(ActivityStatus.DONE);
    }

    @Test
    @DisplayName("승인 없이 답사 시작 불가")
    void invalidTransition() throws Exception {

        assertStatus(ActivityStatus.REPORTED);

        mockMvc.perform(patch("/activity-report/{id}/start-survey", reportId))
                .andExpect(status().isOk());

        // 여전히 REPORTED 이어야 함
        assertStatus(ActivityStatus.REPORTED);
    }

    @Test
    @DisplayName("답사 시작 → 답사 완료 → 모집 가능")
    void approveToRecruit() throws Exception {
        mockMvc.perform(patch("/activity-report/{id}/approve", reportId))
                .andExpect(status().isOk());
        assertStatus(ActivityStatus.APPROVED);

        mockMvc.perform(patch("/activity-report/{id}/start-survey", reportId))
                .andExpect(status().isOk());
        assertStatus(ActivityStatus.SURVEYING);

        mockMvc.perform(patch("/activity-report/{id}/finish-survey", reportId))
                .andExpect(status().isOk());
        assertStatus(ActivityStatus.RECRUITING);
    }




    @Test
    @DisplayName("어떤 상태에서도 취소 가능")
    void cancelAnytime() throws Exception {

        mockMvc.perform(patch("/activity-report/{id}/approve", reportId))
                .andExpect(status().isOk());
        assertStatus(ActivityStatus.APPROVED);

        mockMvc.perform(patch("/activity-report/{id}/cancel", reportId))
                .andExpect(status().isOk());

        assertStatus(ActivityStatus.CANCELED);
    }
}
