package domain.backend.organization.controller;

import domain.backend.organization.domain.entity.Organization;
import domain.backend.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationRepository organizationRepository;

    /**
     * 모든 단체 목록 조회
     */
    @GetMapping
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }
}
