package domain.backend.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "marine-log.exchange";

    public static final String QUEUE_NAME = "marine-log.export.queue";

    public static final String ROUTING_KEY = "marine-log.export";

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue exportQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange marineLogExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding exportBinding(Queue queue, Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY)
                .noargs();
    }
}
