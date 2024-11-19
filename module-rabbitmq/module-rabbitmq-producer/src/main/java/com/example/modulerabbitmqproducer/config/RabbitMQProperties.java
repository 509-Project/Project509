package com.example.modulerabbitmqproducer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "rabbitmq")
@Configuration
@Getter
@Setter
public class RabbitMQProperties {

    private Exchange exchange;
    private Queue queue;
    private String routingKeyName;
    private int ttl;

    @Getter
    @Setter
    public static class Exchange {
        private String name;
        private String deadLetter;
    }

    @Getter
    @Setter
    public static class Queue {
        private String name;
        private String deadLetter;
    }

}