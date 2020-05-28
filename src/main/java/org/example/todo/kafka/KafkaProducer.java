package org.example.todo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.dto.DtoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class KafkaProducer<T extends DtoEntity> {

	@Autowired
	private KafkaTemplate<String, T> kafkaTemplate;

	public void simpleSendMessage(String topic, T message) {
		kafkaTemplate.send(topic, message);
	}

	public void sendMessage(String topic, T message) {
		ListenableFuture<SendResult<String, T>> future =
				kafkaTemplate.send(topic, message);

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
}
