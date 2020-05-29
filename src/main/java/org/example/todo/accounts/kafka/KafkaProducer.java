package org.example.todo.accounts.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.dto.DtoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class KafkaProducer<T extends DtoEntity> {

	private KafkaTemplate<String, T> kafkaTemplate;

	public void simpleSendMessage(String topic, T message) {
		kafkaTemplate.send(topic, message);
	}

	public void sendMessage(String topic, KafkaOperation operation, T message) {

		Message<T> kafkaMessage = MessageBuilder
				.withPayload(message)
				.setHeader(KafkaHeaders.TOPIC, topic)
				//Allows the kafka partition to be set based off the object's uuid so messages for the same entity will
				// always arrive in order.
				.setHeader(KafkaHeaders.MESSAGE_KEY, message.getUuid())
				.setHeader("operation", operation)
				.build();

		ListenableFuture<SendResult<String, T>> future =
				kafkaTemplate.send(kafkaMessage);

		future.addCallback(new ListenableFutureCallback<SendResult<String, T>>() {

			@Override
			public void onSuccess(SendResult<String, T> result) {
				log.debug("Sent message=[" + message +
						"] with offset=[" + result.getRecordMetadata().offset() + "]");
			}
			@Override
			public void onFailure(Throwable ex) {
				log.error("Unable to send message=["
						+ message + "] due to : " + ex.getMessage());
			}
		});
	}

	@Autowired
	public void setKafkaTemplate(KafkaTemplate<String, T> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
}
