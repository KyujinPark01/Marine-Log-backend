package domain.backend.activity.service;

import domain.backend.activity.domain.Activity;
import domain.backend.organization.domain.entity.HostedBy;
import domain.backend.organization.domain.entity.HostedById;
import domain.backend.organization.domain.entity.Organization;
import domain.backend.organization.dto.HostedByDto;
import domain.backend.organization.repository.HostedByRepository;
import domain.backend.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityOrganizationService {
    private final OrganizationRepository organizationRepository;
    private final HostedByRepository hostedByRepository;

    public void saveHostedOrganizations(Activity activity, List<HostedByDto> hostedByList) {

        if (hostedByList == null || hostedByList.isEmpty()) {
            return;
        }

        for (HostedByDto hostedByDto : hostedByList) {

            Organization org = organizationRepository.findById(hostedByDto.getOrgId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("단체가 존재하지 않습니다. id=" + hostedByDto.getOrgId()));

            HostedBy hostedBy = HostedBy.builder()
                    .hostedById(new HostedById(activity.getActivityId(), org.getOrgId()))
                    .activity(activity)
                    .organization(org)
                    .contribution(hostedByDto.getContribution() != null ? hostedByDto.getContribution() : 1.0)
                    .build();

            hostedByRepository.save(hostedBy);

            log.info("활동-단체 매핑 저장 - activityId={}, orgId={}", activity.getActivityId(), org.getOrgId());
        }
    }
}
