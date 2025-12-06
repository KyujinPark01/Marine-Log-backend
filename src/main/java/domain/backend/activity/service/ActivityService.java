package domain.backend.activity.service;


import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.ActivityPicture;
import domain.backend.activity.domain.Status;
import domain.backend.activity.dto.request.ActivityCreateRequest;
import domain.backend.activity.dto.request.ActivityUpdateRequest;
import domain.backend.activity.dto.response.ActivityDetailResponse;
import domain.backend.activity.dto.response.MyActivityListResponse;
import domain.backend.activity.dto.response.PublicActivityListResponse;
import domain.backend.activity.repository.ActivityPictureRepository;
import domain.backend.activity.repository.ActivityRepository;

import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
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
    private final UserRepository userRepository;
    // ==================== Activity 공통 로직 ====================
    // - 소유자 검증(getOwned)
    // - 필드 유효성 검증(validateForPublish)
    // - 부분 업데이트(applyPatch)
    public Activity getOwned(Long activityId, Long userId) {
        Activity a = activityRepository.findById(activityId)
                .orElseThrow(() -> {
                    log.warn(" 활동 조회 실패 - 존재하지 않음, activityId={}, userId={}", activityId, userId);
                    return new IllegalArgumentException("활동이 존재하지 않습니다. id=" + activityId);
                });
        Long ownerId = a.getCreatedBy().getUserId();
        if (!ownerId.equals(userId)) {
            log.warn("권한 없음 - userId={} is not owner of activityId={}", userId, activityId);
            throw new AccessDeniedException("본인이 작성한 글만 조작할 수 있습니다.");
        }
        return a;
    }

    private void applyPatch(Activity a, ActivityUpdateRequest d) {
        a.updateField(d);
    }

    // ==================== 생성 로직 (임시저장 / 게시 공통) ====================

    @Transactional
    public ActivityDetailResponse createAndPublish(ActivityCreateRequest dto, Long userId) {
        // 바로 게시
        Activity a = createActivity(dto, userId, Status.PUBLISHED);
        // 생성 후 상세 정보를 다시 조회하여 반환
        return getOne(a.getActivityId(), userId);
    }

    @Transactional
    public ActivityDetailResponse createDraft(ActivityCreateRequest dto, Long userId) {
        // 임시저장
        Activity a = createActivity(dto, userId, Status.DRAFT);
        // 생성 후 상세 정보를 다시 조회하여 반환
        return getOne(a.getActivityId(), userId);
    }

    /* 임시저장 / 게시 생성 공통 로직 */
    @Transactional
    protected Activity createActivity(ActivityCreateRequest dto, Long userId, Status status) {

        User author = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저 없음. uuid=" + userId));

        log.info("▶ 활동 생성 요청 - authorUserId={}, name={}, status={}", userId, dto.getName(), status);

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
                .status(status)
                .build();

        activityRepository.save(a);

        // 위치 저장
        if (dto.getLat() != null && dto.getLng() != null) {
            locationService.saveForActivity(a, dto.getLat(), dto.getLng());
        }

        // 참여 단체 저장
        activityOrganizationService.saveHostedOrganizations(a, dto.getHostedByList());

        // 수거 쓰레기 저장
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

        // PUBLISHED 일 때만 필수값 검증
        if (status == Status.PUBLISHED) {
            if (a.getName() == null || a.getName().isBlank()) {
                throw new IllegalArgumentException("활동명은 필수입니다.");
            }
            if (a.getActivityDate() == null) {
                throw new IllegalArgumentException("활동 날짜는 필수입니다.");
            }
        }

        log.info("✔ 활동 생성 완료 - activityId={}, status={}", a.getActivityId(), status);
        return a; // 변경된 부분: Activity 객체 자체를 반환
    }

    @Transactional
    public ActivityDetailResponse updateActivity(Long id, ActivityUpdateRequest dto, Long userId) {
        log.info("▶ 활동 수정 요청 - activityId={}, userId={}", id, userId);

        Activity a = getOwned(id, userId);   // 소유자 체크

        // 1) 삭제된 활동은 수정 금지
        if (a.getStatus() == Status.DELETED) {
            throw new IllegalArgumentException("삭제된 활동은 수정할 수 없습니다.");
        }

        // 2) 기본 필드(이름, 설명, 일시, 인원, 무게, 부피 등) 부분 수정
        applyPatch(a, dto);

        // 3) 위치 정보 업데이트
        if (dto.getLat() != null && dto.getLng() != null) {
            locationService.saveForActivity(a, dto.getLat(), dto.getLng());
        }

        // 4) 참여 단체 목록 수정
        if (dto.getHostedByList() != null) {
            activityOrganizationService.saveHostedOrganizations(a, dto.getHostedByList());
        }

        // 5) 수거 쓰레기 목록 수정
        if (dto.getWastes() != null) {
            a.getWastes().clear();

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

        // 6) 상태별 검증
        if (a.getStatus() == Status.PUBLISHED) {
            // 게시글은 필수값 검증
            if (a.getName() == null || a.getName().isBlank()) {
                throw new IllegalArgumentException("활동명은 필수입니다.");
            }
            if (a.getActivityDate() == null) {
                throw new IllegalArgumentException("활동 날짜는 필수입니다.");
            }
        }
        // DRAFT 인 경우에는 필수값 검증 X

        log.info("✔ 활동 수정 완료 - activityId={}, status={}", id, a.getStatus());
        // 수정 후 상세 정보를 다시 조회하여 반환
        return getOne(id, userId);
    }


    // 3) 임시저장 -> 게시
    @Transactional
    public ActivityDetailResponse publish(Long id, Long userId) {
        log.info("▶ 활동 게시 전환 요청 - activityId={}, userId={}", id, userId);

        Activity a = getOwned(id, userId);

        if (a.getStatus() != Status.DRAFT) {
            throw new IllegalArgumentException("초안만 게시할 수 있습니다.");
        }

        // 게시 전 활동명, 활동 날짜 필드 유효성 검증
        if (a.getName() == null || a.getName().isBlank()) {
            throw new IllegalArgumentException("활동명은 필수입니다.");
        }
        if (a.getActivityDate() == null) {
            throw new IllegalArgumentException("활동 날짜는 필수입니다.");
        }

        a.publish();

        log.info("✔ 활동 게시 전환 완료 - activityId={}", id);
        // 게시 후 상세 정보를 다시 조회하여 반환
        return getOne(id, userId);
    }

    // 5) 삭제(임시저장/게시 공통) -> 소프트 삭제
    @Transactional
    public void delete(Long id, Long userId) {
        log.info(" 활동 삭제 요청 - activityId={}, userId={}", id, userId);

        Activity a = getOwned(id,userId);

        a.softDelete();

        log.info(" 활동 소프트 삭제 완료 - activityId={}, userId={}", id, userId);
    }

    //목록 상세 조회
    public ActivityDetailResponse getOne(Long id, Long userIdOrNull) {
        Activity a = activityRepository.findByIdWithAllDetails(id) // 변경된 부분: findByIdWithAllDetails 사용
                .orElseThrow(() -> new IllegalArgumentException("활동이 존재하지 않습니다. id=" + id));

        // draft는 본인만 조회가능
        if (a.getStatus() != Status.PUBLISHED) {
            Long ownerId = a.getCreatedBy().getUserId();
            if (userIdOrNull == null || !ownerId.equals(userIdOrNull)) {
                log.warn("권한 없음 - userId={} is not owner of draft activityId={}", userIdOrNull, id);
                throw new AccessDeniedException("게시되지 않은 활동에 접근할 권한이 없습니다.");
            }
        }

        // 변경된 부분: ActivityDetailResponse.from 사용, a.getPictures() 전달
        return ActivityDetailResponse.from(a, a.getPictures());
    }


    // 목록 조회
    public Page<PublicActivityListResponse> listPublished(Pageable pageable) {
        return activityRepository.findAllByStatus(Status.PUBLISHED, pageable)
                .map(activity -> {
                    String coverUrl = activityPictureRepository
                            .findTopByActivityOrderByPictureIdAsc(activity)
                            .map(ActivityPicture::getPictureUrl)
                            .orElse(null);

                    return PublicActivityListResponse.from(activity, coverUrl);
                });
    }
    //내 활동 목록 조회
    public Page<MyActivityListResponse> listMine(Long userId, Collection<Status> statuses, Pageable pageable) {
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음. userId=" + userId));
        return activityRepository.findAllByCreatedByAndStatusIn(me, statuses, pageable)
                .map(MyActivityListResponse::from);
    }

}

