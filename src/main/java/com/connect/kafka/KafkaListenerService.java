package com.connect.kafka;

import com.connect.buffer.MessageBuffer;
import com.connect.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaListenerService {

    @Autowired
    private MessageBuffer messageBuffer;

    @KafkaListener(topics = "chat", groupId = "chat-group")
    public void consumeEvent(Message message) {
        // logic message is being consumed.
        log.info("Consumed message: {}", message.toString());
        // Here we have to save the message to a shared buffer ok, and we need to save the data to the database.
        messageBuffer.addMessage(message);
    }

}
