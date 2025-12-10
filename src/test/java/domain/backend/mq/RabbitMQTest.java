package domain.backend.mq;

import domain.backend.fileexport.service.ExportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class RabbitMQTest {
    @Autowired
    ExportService exportService;

    @Test
    @DisplayName("RabbitMQ에 메시지가 전달된다") // 실제 메시지 전달 여부 확인은 RabbitMQ 콘솔에서 가능
    public void sendTest() {
        ExportRequest exportRequest = ExportRequest.builder()
                .exportType(ExportRequest.ExportType.ACTIVITY)
                .orgId(1L)
                .activityId(1L)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.now())
                .region1depthName("인천광역시")
                .build();

        exportService.queueDataExport(exportRequest);
    }
}
