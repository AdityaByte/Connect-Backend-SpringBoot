package com.connect.kafka;

import com.connect.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaListenerService {

    @KafkaListener(topics = "chat", groupId = "chat-group")
    public void consumeEvent(Message message) {
        // logic message is being consumed.
        log.info("Consumed message: {}", message.toString());
    }

}
