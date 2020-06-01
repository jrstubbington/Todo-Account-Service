package org.example.todo.accounts.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

	private static final int KAFKA_USERS_TOPIC_USER_PARTITIONS = 10;

	private static final short KAFKA_USERS_TOPIC_REPLICA_COUNT = 1;

	@Bean
	public NewTopic usersTopic() {
		return new NewTopic("users", KAFKA_USERS_TOPIC_USER_PARTITIONS, KAFKA_USERS_TOPIC_REPLICA_COUNT);
	}
}
