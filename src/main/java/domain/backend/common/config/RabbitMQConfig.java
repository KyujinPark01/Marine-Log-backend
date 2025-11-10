package domain.backend.common.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "marine-log.queue";
    public static final String EXCHANGE_NAME = "marine-log.exchange";
    public static final String ROUTING_KEY = "marine-log.key";

    @Bean
    public Queue marineLogQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Exchange marineLogExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding marineLogBinding(Queue marineLogQueue, Exchange marineLogExchange) {
        return BindingBuilder
                .bind(marineLogQueue)
                .to(marineLogExchange)
                .with(ROUTING_KEY)
                .noargs();
    }
}
