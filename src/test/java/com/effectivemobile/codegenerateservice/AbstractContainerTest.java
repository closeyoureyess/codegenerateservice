package com.effectivemobile.codegenerateservice;

import com.redis.testcontainers.RedisContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractContainerTest {

    protected static final RedisContainer REDIS_CONTAINER;
    protected static final KafkaContainer KAFKA_CONTAINER;
    private static final Network NETWORK = Network.newNetwork();

    static {
        KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"))
                .withNetwork(NETWORK);
        REDIS_CONTAINER = new RedisContainer("redis:7.2.0");
        KAFKA_CONTAINER.start();
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));

        registry.add("spring.kafka.consumer.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }
}
