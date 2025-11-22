package domain.backend.dashboard.controller;

import domain.backend.common.dto.ApiResponse;
import domain.backend.dashboard.dto.request.StatFilterRequest;
import domain.backend.dashboard.dto.response.OrgRankingResponse;
import domain.backend.dashboard.service.OrgRankService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class LeaderboardController {

    // 홈 화면의 리더보드를 위한 단체별 폐기물 수거량 순위
    private final OrgRankService orgRankService;
    @Operation(summary = "단체 리더보드", description = "활동량 기준 단체 순위를 조회합니다.")
    @GetMapping("/org-leaderboard")
    public ResponseEntity<ApiResponse<List<OrgRankingResponse>>> leaderboard(StatFilterRequest filter,
                                                                             @RequestParam(defaultValue = "10") int limit) {
        List<OrgRankingResponse> leaderboard = orgRankService.getOrgLeaderboard(filter, Math.max(1, limit));
        return ResponseEntity.ok(ApiResponse.ok(leaderboard));
    }
    //====================================================================================
}
