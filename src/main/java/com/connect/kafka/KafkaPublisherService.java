package com.connect.kafka;

import com.connect.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(Message message) {
        log.info("Message received: {}", message.toString());
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("chat", message);
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("Message sent successfully to {}", result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message: {}", message.toString());
            }
        });
    }

}
