package com.xtra.api.config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class MessagingConfig {
    @Bean
    public Queue streamQueue() {
        return new Queue("stream", false);
    }

    @RabbitListener(queues = "stream")
    public void listen(String in) {
        System.out.println("Message read from myQueue : " + in);
    }
}
