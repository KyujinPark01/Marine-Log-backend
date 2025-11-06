package domain.backend.organization.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostedByDto {
    private Long orgId;        // 단체 ID
    private Double contribution; // 기여도 (선택)
}
