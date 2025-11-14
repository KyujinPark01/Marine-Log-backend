package domain.backend.fileexport.service;

import domain.backend.common.config.RabbitMQConfig;
import domain.backend.fileexport.request.ExportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final RabbitTemplate rabbitTemplate;

    public void queueDataExport(ExportRequest exportRequest) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                exportRequest);
    }
}
