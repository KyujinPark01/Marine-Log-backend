package domain.backend.organization.controller;

import domain.backend.organization.domain.entity.Organization;
import domain.backend.organization.repository.OrganizationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(
        name = "Organization API",
        description = "단체(Organization) 정보 조회 관련 API"
)
@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationRepository organizationRepository;
    @Operation(summary = "전체 단체 조회", description = "등록된 모든 단체 정보를 조회합니다.")
    @GetMapping
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }
}
