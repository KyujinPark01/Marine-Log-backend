package domain.backend.dashboard.service;

import domain.backend.dashboard.dao.OrgRankRepository;
import domain.backend.dashboard.dto.request.StatFilterRequest;
import domain.backend.dashboard.dto.response.OrgRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrgRankService {
    private final OrgRankRepository orgDashRepository;

    public List<OrgRankingResponse> getOrgLeaderboard(StatFilterRequest f, int limit) {
        return orgDashRepository.orgRanking(
                        f.getStart(),
                        f.getEnd(),
                        f.getOrgId(),
                        limit)
                .stream()
                .map(r -> new OrgRankingResponse(r.getOrgId(), r.getOrgName(), r.getTotalKg() == null ? 0.0 : r.getTotalKg()))
                .toList();
    }
}
