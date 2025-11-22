package domain.backend.activity.service;


import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.Status;
import domain.backend.activity.dto.request.ActivityCreateRequest;
import domain.backend.activity.dto.request.ActivityUpdateRequest;
import domain.backend.activity.dto.response.ActivityResponse;
import domain.backend.activity.repository.ActivityPictureRepository;
import domain.backend.activity.repository.ActivityRepository;

import domain.backend.auth.domain.user.User;
import domain.backend.waste.domain.Waste;
import domain.backend.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collection;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final LocationService locationService;
    private final ActivityPictureRepository activityPictureRepository;
    private final ActivityOrganizationService activityOrganizationService;
    // ==================== Activity 공통 로직 ====================
    // - 소유자 검증(getOwned)
    // - 필드 유효성 검증(validateForPublish)
    // - 부분 업데이트(applyPatch)
    private Activity getOwned(Long id, Long userId) {
        Activity a = activityRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(" 활동 조회 실패 - 존재하지 않음, activityId={}, userId={}", id, userId);
                    return new IllegalArgumentException("활동이 존재하지 않습니다. id=" + id);
                });
        if (!a.getCreatedBy().getUserId().equals(userId)) {
                log.warn("권한 없음 - userId={} is not owner of activityId={}", userId, id);
                throw new AccessDeniedException("본인이 작성한 글만 조작할 수 있습니다.");

        }
        return a;
    }
    private void ensure(boolean cond, String msg) {
        if (!cond) throw new IllegalStateException(msg);
    }
    private void applyPatch(Activity a, ActivityUpdateRequest d) {
        a.updateField(d);
    }
    private void validateForPublish(Activity a) {
        //핵심 필드(제목,날짜) 검증
        ensure(a.getName() != null && !a.getName().isBlank(), "제목은 필수입니다.");
        ensure(a.getActivityDate() != null, "활동 날짜는 필수입니다.");
    }


    // 1) 게시(바로 게시 생성)
    @Transactional
    public ActivityResponse createAndPublish(ActivityCreateRequest dto, User author) {
        log.info("▶ 활동 즉시 게시 생성 요청 - authorId={}, name={}", author.getUserId(), dto.getName());
        Activity a = Activity.builder()
                .createdBy(author)
                .name(dto.getName())
                .description(dto.getDescription())
                .activityDate(dto.getActivityDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .participants(dto.getParticipants())
                .weightKg(dto.getWeightKg())
                .volumeL(dto.getVolumeL())
                .status(Status.DRAFT)
                .build();

        a.publish();
        activityRepository.save(a);

        // 위치 저장
        if (dto.getLat() != null && dto.getLng() != null) {
            locationService.saveForActivity(a, dto.getLat(), dto.getLng());
        }

        // 참여 단체 저장
        activityOrganizationService.saveHostedOrganizations(a, dto.getHostedByList());

        if (dto.getWastes() != null && !dto.getWastes().isEmpty()) {
            List<Waste> wasteEntities = dto.getWastes().stream()
                    .map(w -> Waste.builder()
                            .activity(a)
                            .type(w.getType())
                            .pictureUrl(w.getPictureUrl())
                            .weightKg(w.getWeightKg())
                            .volumeL(w.getVolumeL())
                            .build())
                    .toList();

            a.getWastes().addAll(wasteEntities);
        }
        log.info("✔ 활동 게시 완료 - activityId={}", a.getActivityId());
        return ActivityResponse.from(activityRepository.save(a));
    }

    // ───────── 임시저장 ─────────
    @Transactional
    public ActivityResponse createDraft(ActivityCreateRequest dto, User author) {
        log.info("▶ 활동 임시저장 요청 - authorId={}, name={}", author.getUserId(), dto.getName());
        Activity a = Activity.builder()
                .createdBy(author)
                .name(dto.getName())
                .description(dto.getDescription())
                .activityDate(dto.getActivityDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .participants(dto.getParticipants())
                .weightKg(dto.getWeightKg())
                .volumeL(dto.getVolumeL())
                .status(Status.DRAFT)
                .build();

        activityRepository.save(a);

        activityOrganizationService.saveHostedOrganizations(a, dto.getHostedByList());

        log.info("✔ 활동 임시저장 완료 - activityId={}", a.getActivityId());
        return ActivityResponse.from(a);
    }

    // 3) 임시저장 -> 게시
    @Transactional
    public ActivityResponse publish(Long id, Long userId) {
        log.info("▶ 활동 게시 전환 요청 - activityId={}, userId={}", id, userId);

        Activity a = getOwned(id, userId);
        ensure(a.getStatus() == Status.DRAFT, "초안만 게시할 수 있습니다.");
        validateForPublish(a);
        a.publish();

        log.info("✔ 활동 게시 전환 완료 - activityId={}", id);
        return ActivityResponse.from(a);
    }

    // 4) 게시 -> 수정 (부분 수정)
    @Transactional
    public ActivityResponse updatePublished(Long id, ActivityUpdateRequest dto, Long userId) {
        log.info(" 활동 수정 요청 - activityId={}, userId={}", id, userId);

        Activity a = getOwned(id, userId);
        ensure(a.getStatus() == Status.PUBLISHED, "게시된 글만 수정할 수 있습니다.");
        applyPatch(a, dto);
        validateForPublish(a);      // 핵심 필드(제목, 활동 날짜) 검증

        log.info("✔ 활동 수정 완료 - activityId={}", id);
        return ActivityResponse.from(a);
    }

    // 5) 삭제(임시저장/게시 공통) -> 소프트 삭제
    @Transactional
    public void delete(Long id, Long userId) {
        log.info(" 활동 삭제 요청 - activityId={}, userId={}", id, userId);

        Activity a = getOwned(id, userId);
        ensure(a.getStatus() != Status.DELETED, "이미 삭제된 글입니다.");
        a.softDelete();

        log.info(" 활동 소프트 삭제 완료 - activityId={}, userId={}", id, userId);
    }

    //목록 상세 조회
    public ActivityResponse getOne(Long id, Long userIdOrNull) {
        Activity a = activityRepository.findByIdWithHostedBy(id)
                .orElseThrow(() -> new IllegalArgumentException("활동이 존재하지 않습니다. id=" + id));

        if (a.getStatus() != Status.PUBLISHED) {
            ensure(userIdOrNull != null && a.getCreatedBy().getUserId().equals(userIdOrNull),
                    "접근 권한이 없습니다.");
        }

        return ActivityResponse.from(a); // ✅ 이제 hostedByList 포함됨
    }

    @Transactional(readOnly = true)
    public Activity getOwnedEntity(Long id, Long userId) {
        return getOwned(id, userId);
    }

//    public Page<ActivityResponse> listPublished(Pageable pageable) {
//        return activityRepository.findAllByStatus(Status.PUBLISHED, pageable)
//                .map(ActivityResponse::from);
//    }

    // 목록 조회
    public Page<ActivityResponse> listPublished(Pageable pageable) {
        return activityRepository.findAllByStatus(Status.PUBLISHED, pageable)
                .map(activity -> {
                    // ✅ 대표 사진 1장 가져오기
                    String coverUrl = activityPictureRepository
                            .findFirstPictureUrlByActivityId(activity.getActivityId());

                    // ✅ ActivityResponse로 변환
                    return ActivityResponse.fromWithCover(activity, coverUrl);
                });
    }
    //내 활동 목록 조회
    public Page<ActivityResponse> listMine(User me, Collection<Status> statuses, Pageable pageable) {
        return activityRepository.findAllByCreatedByAndStatusIn(me, statuses, pageable)
                .map(ActivityResponse::from);
    }
}

