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
import domain.backend.organization.domain.entity.HostedBy;
import domain.backend.organization.domain.entity.HostedById;
import domain.backend.organization.domain.entity.Organization;
import domain.backend.organization.dto.HostedByDto;
import domain.backend.organization.repository.HostedByRepository;
import domain.backend.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final LocationService locationService;
    private final ActivityPictureRepository activityPictureRepository;
    private final OrganizationRepository organizationRepository;
    private final HostedByRepository hostedByRepository;
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

        // ✅ 위치 저장
        if (dto.getLat() != null && dto.getLng() != null) {
            locationService.saveForActivity(a, dto.getLat(), dto.getLng());
        }

        // ✅ 참여 단체 저장 (Activity 수정 없이 Repository 직접 저장)
        if (dto.getHostedByList() != null) {
            for (HostedByDto h : dto.getHostedByList()) {
                Organization org = organizationRepository.findById(h.getOrgId())
                        .orElseThrow(() -> new IllegalArgumentException("단체가 존재하지 않습니다. id=" + h.getOrgId()));

                HostedBy hostedBy = HostedBy.builder()
                        .hostedById(new HostedById(a.getActivityId(), org.getOrgId()))
                        .activity(a)
                        .organization(org)
                        .contribution(h.getContribution() != null ? h.getContribution() : 1.0)
                        .build();

                hostedByRepository.save(hostedBy); // ✅ 여기서 직접 저장
            }
        }
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

        return ActivityResponse.from(activityRepository.save(a));
    }

    // ───────── 임시저장 ─────────
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

        activityRepository.save(a);

        if (dto.getHostedByList() != null) {
            for (HostedByDto h : dto.getHostedByList()) {
                Organization org = organizationRepository.findById(h.getOrgId())
                        .orElseThrow(() -> new IllegalArgumentException("단체가 존재하지 않습니다. id=" + h.getOrgId()));

                HostedBy hostedBy = HostedBy.builder()
                        .hostedById(new HostedById(a.getActivityId(), org.getOrgId()))
                        .activity(a)
                        .organization(org)
                        .contribution(h.getContribution() != null ? h.getContribution() : 1.0)
                        .build();

                hostedByRepository.save(hostedBy); // ✅ Activity 수정 없이 저장
            }
        }

        return ActivityResponse.from(a);
    }

    // 3) 임시저장 -> 게시
    @Transactional
    public ActivityResponse publish(Long id, Long userId) {
        Activity a = getOwned(id, userId);
        ensure(a.getStatus() == Status.DRAFT, "초안만 게시할 수 있습니다.");
        validateForPublish(a);
        a.publish();
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
        a.softDelete();
    }

    //조회
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

    public Page<ActivityResponse> listMine(User me, Collection<Status> statuses, Pageable pageable) {
        return activityRepository.findAllByCreatedByAndStatusIn(me, statuses, pageable)
                .map(ActivityResponse::from);
    }
}

