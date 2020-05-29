package org.example.todo.accounts.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * This class will create a topic in the kafka cluster specified in the
 * application.yml file. This lives in the producer rather than the consumer
 * to avoid the producer not being able to publish anywhere, rather than the consumer
 * not consuming from an otherwise-empty topic. Topics will store unread objects until
 * consumers consume them
 */
@Configuration
public class KafkaTopicConfig {

	private static final int KAFKA_TOPIC_USER_PARTITIONS = 10;

	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;

	@Bean
	public KafkaAdmin kafkaAdmin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		return new KafkaAdmin(configs);
	}

	@Bean
	public NewTopic topic1() {
		return new NewTopic("users", KAFKA_TOPIC_USER_PARTITIONS, (short) 1);
	}
}
