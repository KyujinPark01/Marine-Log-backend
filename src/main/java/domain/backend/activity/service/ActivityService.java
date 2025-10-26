package domain.backend.activity.service;


import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.Status;
import domain.backend.activity.dto.request.ActivityCreateRequest;
import domain.backend.activity.dto.request.ActivityUpdateRequest;
import domain.backend.activity.dto.response.ActivityResponse;
import domain.backend.activity.repository.ActivityRepository;

import domain.backend.auth.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {

    private final ActivityRepository activityRepository;

    // ==================== Activity 공통 로직 ====================
    // - 소유자 검증(getOwned)
    // - 필드 유효성 검증(validateForPublish)
    // - 부분 업데이트(applyPatch)
    private Activity getOwned(Long id, Long userId) {
        Activity a = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("활동이 존재하지 않습니다. id=" + id));
        if (!a.getCreatedBy().getUserId().equals(userId)) {
            try {
                throw new AccessDeniedException("본인이 작성한 글만 조작할 수 있습니다.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }
        return a;
    }
    private void ensure(boolean cond, String msg) {
        if (!cond) throw new IllegalStateException(msg);
    }
    private void applyPatch(Activity a, ActivityUpdateRequest d) {
        if (d.getName() != null) a.setName(d.getName());
        if (d.getDescription() != null) a.setDescription(d.getDescription());
        if (d.getActivityDate() != null) a.setActivityDate(d.getActivityDate());
        if (d.getStartTime() != null) a.setStartTime(d.getStartTime());
        if (d.getEndTime() != null) a.setEndTime(d.getEndTime());
        if (d.getParticipants() != null) a.setParticipants(d.getParticipants());
        if (d.getWeightKg() != null) a.setWeightKg(d.getWeightKg());
        if (d.getVolumeL() != null) a.setVolumeL(d.getVolumeL());
    }
    private void validateForPublish(Activity a) {
        //핵심 필드(제목,날짜) 검증
        ensure(a.getName() != null && !a.getName().isBlank(), "제목은 필수입니다.");
        ensure(a.getActivityDate() != null, "활동 날짜는 필수입니다.");
    }


    // 1) 게시(바로 게시 생성)
    @Transactional
    public ActivityResponse createAndPublish(ActivityCreateRequest dto, User author) {
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
                .status(Status.DRAFT) // 생성 후 검증, 곧 PUBLISHED로 전환
                .build();

        validateForPublish(a);
        a.setStatus(Status.PUBLISHED);
        return ActivityResponse.from(activityRepository.save(a));
    }

    // 2) 임시저장(초안 생성)
    @Transactional
    public ActivityResponse createDraft(ActivityCreateRequest dto, User author) {
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
        return ActivityResponse.from(activityRepository.save(a));
    }

    // 3) 임시저장 -> 게시
    @Transactional
    public ActivityResponse publish(Long id, Long userId) {
        Activity a = getOwned(id, userId);
        ensure(a.getStatus() == Status.DRAFT, "초안만 게시할 수 있습니다.");
        validateForPublish(a);
        a.setStatus(Status.PUBLISHED);
        return ActivityResponse.from(a);
    }

    // 4) 게시 -> 수정 (부분 수정)
    @Transactional
    public ActivityResponse updatePublished(Long id, ActivityUpdateRequest dto, Long userId) {
        Activity a = getOwned(id, userId);
        ensure(a.getStatus() == Status.PUBLISHED, "게시된 글만 수정할 수 있습니다.");
        applyPatch(a, dto);
        validateForPublish(a);      // 핵심 필드(제목, 활동 날짜) 검증
        return ActivityResponse.from(a);
    }

    // 5) 삭제(임시저장/게시 공통) -> 소프트 삭제
    @Transactional
    public void delete(Long id, Long userId) {
        Activity a = getOwned(id, userId);
        ensure(a.getStatus() != Status.DELETED, "이미 삭제된 글입니다.");
        a.setStatus(Status.DELETED);
    }

    //조회
    public ActivityResponse getOne(Long id, Long userIdOrNull) {
        Activity a = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("활동이 존재하지 않습니다. id=" + id));

        // 권한 정책: PUBLISHED는 모두 조회 가능, DRAFT/DELETED는 작성자만
        if (a.getStatus() != Status.PUBLISHED) {
            ensure(userIdOrNull != null && a.getCreatedBy().getUserId().equals(userIdOrNull),
                    "접근 권한이 없습니다.");
        }
        return ActivityResponse.from(a);
    }

    public Page<ActivityResponse> listPublished(Pageable pageable) {
        return activityRepository.findAllByStatus(Status.PUBLISHED, pageable)
                .map(ActivityResponse::from);
    }

    public Page<ActivityResponse> listMine(User me, Collection<Status> statuses, Pageable pageable) {
        return activityRepository.findAllByCreatedByAndStatusIn(me, statuses, pageable)
                .map(ActivityResponse::from);
    }
}

