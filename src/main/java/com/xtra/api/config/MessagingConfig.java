package com.xtra.api.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class MessagingConfig {
    @Bean
    public Queue streamStatusQueue() {
        var args = new HashMap<String, Object>();
        args.put("x-message-ttl", 10000);
        return new Queue("streamStatus", false, false, false, args);
    }

    @Bean
    public Queue connectionsQueue() {
        var args = new HashMap<String, Object>();
        args.put("x-message-ttl", 10000);
        return new Queue("connections", false, false, false, args);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new Jackson2JsonMessageConverter(mapper);
    }

}
