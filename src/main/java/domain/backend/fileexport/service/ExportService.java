package domain.backend.fileexport.service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import domain.backend.fileexport.domain.ExportRepository;
import domain.backend.fileexport.dto.ActivityCsvResponse;
import domain.backend.fileexport.dto.WasteCsvResponse;
import domain.backend.fileexport.projection.ActivityCsvProjection;
import domain.backend.fileexport.projection.WasteCsvProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final ExportRepository exportRepository;

    public ByteArrayResource ActivityExport(LocalDate startDate, LocalDate endDate) {

        List<ActivityCsvProjection> projections = exportRepository.getActivityByUserCsv(startDate, endDate);

        List<ActivityCsvResponse> activityCsvResponses = projections.stream().map(
                projection -> ActivityCsvResponse.builder()
                        .name(projection.getName())
                        .description(projection.getDescription())
                        .activityDate(projection.getActivityDate())
                        .startTime(projection.getStartTime())
                        .endTime(projection.getEndTime())
                        .participants(projection.getParticipants())
                        .weightKg(projection.getWeightKg())
                        .volumeL(projection.getVolumeL())
                        .address(projection.getAddress())
                        .build()
        ).toList();

        StringWriter sw = new StringWriter();

        StatefulBeanToCsv<ActivityCsvResponse> beanToCsv = new StatefulBeanToCsvBuilder<ActivityCsvResponse>(sw)
                .withSeparator(',')
                .build();

        try {
            beanToCsv.write(activityCsvResponses);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException(e.getMessage());
        }

        return new ByteArrayResource(sw.toString().getBytes(StandardCharsets.UTF_8));
    }


    public ByteArrayResource WasteExport(String region, LocalDate startDate, LocalDate endDate) {

        List<WasteCsvProjection> projections = exportRepository.getWasteByRegionCsv(region, startDate, endDate);

        List<WasteCsvResponse> wasteCsvResponses = projections.stream().map(
                projection -> WasteCsvResponse.builder()
                        .region(projection.getRegion())
                        .type(projection.getType())
                        .specificType(projection.getSpecificType())
                        .weightKg(projection.getWeightKg())
                        .volumeL(projection.getVolumeL())
                        .build()
        ).toList();

        StringWriter sw = new StringWriter();

        StatefulBeanToCsv<WasteCsvResponse> beanToCsv = new StatefulBeanToCsvBuilder<WasteCsvResponse>(sw)
                .withSeparator(',')
                .build();

        try {
            beanToCsv.write(wasteCsvResponses);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException(e.getMessage());
        }

        return new ByteArrayResource(sw.toString().getBytes(StandardCharsets.UTF_8));
    }
}
