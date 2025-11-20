package domain.backend.dashboard.service;

import domain.backend.dashboard.dao.StatFromSelectedAreaRepository;
import domain.backend.dashboard.dto.response.MaterialBreakdownResponse;
import domain.backend.dashboard.dto.response.TimeSeriesPointResponse;
import domain.backend.dashboard.dto.request.RegionRequest;
import domain.backend.dashboard.dto.response.RegionDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetStatFromSelectedService {

    private final StatFromSelectedAreaRepository statFromSelectedAreaRepository;

    @Transactional(readOnly = true)
    public RegionDashboardResponse getStat(RegionRequest request) {
        List<Long> activityIds = statFromSelectedAreaRepository.findActivityIdsInBounds(
                request.getSwLat(), request.getSwLng(), request.getNeLat(), request.getNeLng()
        );

        if (activityIds.isEmpty()) {
            return new RegionDashboardResponse(0.0, 0, List.of(), List.of());
        }

        StatFromSelectedAreaRepository.WasteSummaryInRegion summary = statFromSelectedAreaRepository.getSummaryInActivities(activityIds);
        List<StatFromSelectedAreaRepository.MaterialBreakdownInRegion> materialBreakdowns = statFromSelectedAreaRepository.getMaterialBreakdownInActivities(activityIds);
        List<StatFromSelectedAreaRepository.MonthlyTrendInRegion> monthlyTrends = statFromSelectedAreaRepository.getMonthlyTrendInActivities(activityIds);

        double totalWeightKg = summary.getTotalWeightKg() == null ? 0.0 : summary.getTotalWeightKg();
        long wasteCountLong = summary.getWasteCount() == null ? 0L : summary.getWasteCount();
        int wasteCount = (int) Math.min(Integer.MAX_VALUE, wasteCountLong);

        List<MaterialBreakdownResponse> materials = materialBreakdowns.stream()
                .map(m -> new MaterialBreakdownResponse(
                        m.getMaterialType(),
                        m.getTotalKg() == null ? 0.0 : m.getTotalKg(),
                        null
                ))
                .collect(Collectors.toList());

        List<TimeSeriesPointResponse> trend = monthlyTrends.stream()
                .map(t -> new TimeSeriesPointResponse(
                        t.getMonth(),
                        t.getTotalKg() == null ? 0.0 : t.getTotalKg(),
                        null
                ))
                .collect(Collectors.toList());

        return new RegionDashboardResponse(
                totalWeightKg,
                wasteCount,
                materials,
                trend
        );
    }
}
