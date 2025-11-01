package domain.backend.dashboard.controller;

import domain.backend.dashboard.dto.*;
import domain.backend.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryDto summary(DashboardFilter filter) {
        return dashboardService.getSummary(filter);
    }

    @GetMapping("/timeseries")
    public List<MonthlySeriesDto> timeseries(DashboardFilter filter) {
        return dashboardService.getTimeSeries(filter);
    }

    @GetMapping("/material-breakdown")
    public List<MaterialShareDto> material(DashboardFilter filter) {
        return dashboardService.getMaterialBreakdown(filter);
    }

    @GetMapping("/org-leaderboard")
    public List<OrgRankingDto> leaderboard(DashboardFilter filter,
                                           @RequestParam(defaultValue = "10") int limit) {
        return dashboardService.getOrgLeaderboard(filter, Math.max(1, limit));
    }
}

