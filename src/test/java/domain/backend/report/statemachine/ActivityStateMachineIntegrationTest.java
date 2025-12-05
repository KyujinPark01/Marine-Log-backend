package domain.backend.report.statemachine;

import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import domain.backend.organization.domain.entity.Organization;
import domain.backend.organization.repository.OrganizationRepository;
import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.entity.Collaboration;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.enums.CollaborationStatus;
import domain.backend.report.domain.repository.ActivityReportRepository;
import domain.backend.report.domain.repository.ActivityStatusHistoryRepository;
import domain.backend.report.domain.repository.CollaborationRepository;
import domain.backend.report.statemachine.service.ActivityStateMachineService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 🔥 복구: Action의 DB 저장을 위해 필수
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ActivityStateMachineIntegrationTest {

    @Autowired
    private ActivityStateMachineService stateMachineService;

    @Autowired
    private ActivityReportRepository reportRepository;

    @Autowired
    private CollaborationRepository collaborationRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityStatusHistoryRepository historyRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EntityManager entityManager;

    private ActivityReport report;
    private User hostUser;
    private User partnerUser;
    private Organization hostOrg;
    private Organization partnerOrg;

    @BeforeEach
    void setup() {
        // @Transactional이 있으므로 자동 롤백되지만, 명시적 초기화 유지
        collaborationRepository.deleteAll();
        historyRepository.deleteAll();
        reportRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();

        hostOrg = organizationRepository.save(
                Organization.builder()
                        .name("호스트단체")
                        .location("서울 강남구")
                        .description("호스트 단체입니다.")
                        .build()
        );

        partnerOrg = organizationRepository.save(
                Organization.builder()
                        .name("협력단체")
                        .location("서울 종로구")
                        .description("협력 단체입니다.")
                        .build()
        );

        hostUser = userRepository.save(
                User.builder()
                        .email("host@test.com")
                        .name("호스트")
                        .role(Role.HOST)
                        .organization(hostOrg)
                        .build()
        );

        partnerUser = userRepository.save(
                User.builder()
                        .email("partner@test.com")
                        .name("파트너")
                        .role(Role.HOST)
                        .organization(partnerOrg)
                        .build()
        );

        report = reportRepository.save(
                ActivityReport.builder()
                        .reporter(hostUser)
                        .title("테스트 제보")
                        .reportedReason("테스트 이유")
                        .reporterOccupation("학생")
                        .status(ActivityStatus.REPORTED)
                        .photoUrl("test")
                        .latitude(37.1)
                        .longitude(127.1)
                        .build()
        );

        entityManager.flush();
        entityManager.clear();

        report = reportRepository.findById(report.getActivityReportId()).orElseThrow();
    }

    private void send(Long id, ActivityEvent event, User actor) throws Exception {
        stateMachineService.sendEvent(
                id,
                event,
                actor,
                headers -> {
                    headers.setHeader("reportId", id);
                    headers.setHeader("user", actor);
                }
        );
        Thread.sleep(100);
    }

    private void send(Long id, ActivityEvent event, User actor, Long partnerOrgId) throws Exception {
        stateMachineService.sendEvent(
                id,
                event,
                actor,
                headers -> {
                    headers.setHeader("reportId", id);
                    headers.setHeader("user", actor);
                    // Guard와 Action에서 식별용으로 사용
                    headers.setHeader("partnerOrgId", partnerOrgId);
                }
        );
        Thread.sleep(100);
    }

    private ActivityStatus status() {
        // 🔥 중요: Action이 변경한 내용을 DB에 반영(flush)한 뒤, 캐시를 비워(clear) DB에서 새로 읽어옴
        entityManager.flush();
        entityManager.clear();
        return reportRepository.findById(report.getActivityReportId())
                .orElseThrow()
                .getStatus();
    }

    private void waitForStatus(ActivityStatus expectedStatus) throws InterruptedException {
        int maxRetries = 20;
        int count = 0;
        while (count < maxRetries) {
            // status() 내부에서 flush & clear를 수행하므로 최신 상태 조회 보장
            if (status() == expectedStatus) return;
            Thread.sleep(100);
            count++;
        }
    }

    private void waitForCollaboration() throws InterruptedException {
        int maxRetries = 20;
        while (maxRetries > 0) {
            entityManager.flush(); // DB 반영
            entityManager.clear(); // 캐시 초기화
            if (!collaborationRepository.findAll().isEmpty()) return;
            Thread.sleep(100);
            maxRetries--;
        }
    }

    private void waitForCollaborationStatus(CollaborationStatus expectedStatus) throws InterruptedException {
        int maxRetries = 30;
        while (maxRetries > 0) {
            entityManager.flush();
            entityManager.clear();

            List<Collaboration> collabs = collaborationRepository.findAll();
            if (!collabs.isEmpty() && collabs.get(0).getStatus() == expectedStatus) {
                return;
            }
            Thread.sleep(100);
            maxRetries--;
        }
    }

    // ================= 테스트 시작 =================

    @Test
    @Order(1)
    @DisplayName("1. 전체 정상 흐름 테스트 → 승인 → 답사 → 모집 → 진행 → 완료")
    void fullFlowTest() throws Exception {

        send(report.getActivityReportId(), ActivityEvent.APPROVE, hostUser);
        waitForStatus(ActivityStatus.APPROVED);
        assertThat(status()).isEqualTo(ActivityStatus.APPROVED);

        send(report.getActivityReportId(), ActivityEvent.START_SURVEY, hostUser);
        waitForStatus(ActivityStatus.SURVEYING);
        assertThat(status()).isEqualTo(ActivityStatus.SURVEYING);

        send(report.getActivityReportId(), ActivityEvent.FINISH_SURVEY, hostUser);
        waitForStatus(ActivityStatus.RECRUITING);
        assertThat(status()).isEqualTo(ActivityStatus.RECRUITING);

        send(report.getActivityReportId(), ActivityEvent.START_ACTIVITY, hostUser);
        waitForStatus(ActivityStatus.IN_PROGRESS);
        assertThat(status()).isEqualTo(ActivityStatus.IN_PROGRESS);

        send(report.getActivityReportId(), ActivityEvent.FINISH_ACTIVITY, hostUser);
        waitForStatus(ActivityStatus.DONE);
        assertThat(status()).isEqualTo(ActivityStatus.DONE);
    }

    @Test
    @Order(2)
    @DisplayName("2. 승인 없이 답사 시작 불가 (Guard 작동 확인)")
    void invalidStartSurvey() throws Exception {
        assertThat(status()).isEqualTo(ActivityStatus.REPORTED);

        send(report.getActivityReportId(), ActivityEvent.START_SURVEY, hostUser);

        assertThat(status()).isEqualTo(ActivityStatus.REPORTED);
    }

    @Test
    @Order(3)
    @DisplayName("3. 답사 중 연합 요청 발생 → Collaboration 엔티티 저장 확인")
    void collaborationRequestTest() throws Exception {

        send(report.getActivityReportId(), ActivityEvent.APPROVE, hostUser);
        waitForStatus(ActivityStatus.APPROVED);

        send(report.getActivityReportId(), ActivityEvent.START_SURVEY, hostUser);
        waitForStatus(ActivityStatus.SURVEYING);

        assertThat(status()).isEqualTo(ActivityStatus.SURVEYING);

        send(report.getActivityReportId(), ActivityEvent.COLLAB_REQUEST, hostUser, partnerOrg.getOrgId());

        waitForCollaboration();

        List<Collaboration> collabs = collaborationRepository.findAll();
        assertThat(collabs).isNotEmpty();

        Collaboration collab = collabs.get(0);

        assertThat(collab.getActivityReport().getActivityReportId()).isEqualTo(report.getActivityReportId());
        assertThat(collab.getTargetOrg().getOrgId()).isEqualTo(partnerOrg.getOrgId());
        assertThat(collab.getStatus()).isEqualTo(CollaborationStatus.REQUESTED);
    }

    @Test
    @Order(4)
    @DisplayName("4. 연합 요청 승인 테스트")
    void collaborationApproveTest() throws Exception {

        send(report.getActivityReportId(), ActivityEvent.APPROVE, hostUser);
        waitForStatus(ActivityStatus.APPROVED);

        send(report.getActivityReportId(), ActivityEvent.START_SURVEY, hostUser);
        waitForStatus(ActivityStatus.SURVEYING);

        send(report.getActivityReportId(), ActivityEvent.COLLAB_REQUEST, hostUser, partnerOrg.getOrgId());
        waitForCollaboration();

        partnerUser = userRepository.findById(partnerUser.getUserId()).orElseThrow();

        send(report.getActivityReportId(), ActivityEvent.COLLAB_APPROVE, partnerUser, partnerOrg.getOrgId());

        waitForCollaborationStatus(CollaborationStatus.APPROVED);

        Collaboration updatedCollab = collaborationRepository.findAll().get(0);
        assertThat(updatedCollab.getStatus()).isEqualTo(CollaborationStatus.APPROVED);
    }

    @Test
    @Order(5)
    @DisplayName("5. 취소 기능 → 어느 상태에서도 가능해야 함")
    void cancelTest() throws Exception {

        send(report.getActivityReportId(), ActivityEvent.APPROVE, hostUser);
        waitForStatus(ActivityStatus.APPROVED);
        assertThat(status()).isEqualTo(ActivityStatus.APPROVED);

        send(report.getActivityReportId(), ActivityEvent.CANCEL, hostUser);
        waitForStatus(ActivityStatus.CANCELED);

        assertThat(status()).isEqualTo(ActivityStatus.CANCELED);
    }
}