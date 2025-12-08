package domain.backend.fileexport.service;

import domain.backend.common.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final RabbitTemplate rabbitTemplate;

    public void queueActivityExport(String scope,
                                    LocalDate startDate,
                                    LocalDate endDate,
                                    UserPrincipal userPrincipal) {

        rabbitTemplate.convertAndSend(null);
    }


    public void queueWasteExport(Long activityId,
                                 String region,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 UserPrincipal userPrincipal) {

    }
}
