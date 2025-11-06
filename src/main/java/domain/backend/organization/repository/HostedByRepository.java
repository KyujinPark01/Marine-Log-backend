package domain.backend.organization.repository;

import domain.backend.organization.domain.entity.HostedBy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostedByRepository extends JpaRepository<HostedBy,Long> {
}
