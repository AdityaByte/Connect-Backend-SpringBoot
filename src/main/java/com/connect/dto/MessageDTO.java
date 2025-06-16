package com.connect.dto;

import com.connect.model.Message;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String sender;
    private String message;
    private LocalDateTime timeStamp;

    public MessageDTO(Message message) {
        this.sender = message.getSender();
        this.message = message.getMessage();
        this.timeStamp = message.getTimeStamp();
    }
}
