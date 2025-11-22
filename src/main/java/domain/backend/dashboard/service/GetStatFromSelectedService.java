package domain.backend.dashboard.service;

import domain.backend.dashboard.repository.StatFromSelectedAreaRepository;
import domain.backend.dashboard.dto.response.MaterialBreakdownResponse;
import domain.backend.dashboard.dto.response.TimeSeriesPointResponse;
import domain.backend.dashboard.dto.request.RegionRequest;
import domain.backend.dashboard.dto.response.RegionStatResponse;
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
    public RegionStatResponse getStat(RegionRequest request) {
        Double swLat = request.getSwLat();
        Double swLng = request.getSwLng();
        Double neLat = request.getNeLat();
        Double neLng = request.getNeLng();

        // 1. 총 폐기물 수거량
        StatFromSelectedAreaRepository.WasteSummaryInRegion summary =
                statFromSelectedAreaRepository.getSummaryInBounds(swLat, swLng, neLat, neLng);
        List<StatFromSelectedAreaRepository.MaterialBreakdownInRegion> materialBreakdowns =
                statFromSelectedAreaRepository.getMaterialBreakdownInBounds(swLat, swLng, neLat, neLng);
        List<StatFromSelectedAreaRepository.MonthlyTrendInRegion> monthlyTrends =
                statFromSelectedAreaRepository.getMonthlyTrendInBounds(swLat, swLng, neLat, neLng);

        double totalWeightKg = (summary == null || summary.getTotalWeightKg() == null) ? 0.0 : summary.getTotalWeightKg();
        long wasteCountLong = (summary == null || summary.getWasteCount() == null) ? 0L : summary.getWasteCount();
        int wasteCount = (int) Math.min(Integer.MAX_VALUE, wasteCountLong);

        // 2. 폐기물 분류 파이 차트
        List<MaterialBreakdownResponse> materials = materialBreakdowns.stream()
                .map(m -> new MaterialBreakdownResponse(
                        m.getMaterialType(),
                        m.getTotalKg() == null ? 0.0 : m.getTotalKg(),
                        null
                ))
                .collect(Collectors.toList());

        // 3. 월별 폐기물 수거량 추이
        List<TimeSeriesPointResponse> trend = monthlyTrends.stream()
                .map(t -> new TimeSeriesPointResponse(
                        t.getMonth(),
                        t.getTotalKg() == null ? 0.0 : t.getTotalKg(),
                        null
                ))
                .collect(Collectors.toList());

        return new RegionStatResponse(
                totalWeightKg,
                wasteCount,
                materials,
                trend
        );
    }
}
