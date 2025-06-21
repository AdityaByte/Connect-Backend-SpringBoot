package com.connect.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Document(collection = "messages")
public class Message {
    @Id
    private ObjectId id;

    private ObjectId roomId;
    private String sender;
    private String message;
    private LocalDateTime timeStamp;

    public Message(ObjectId roomId, String sender, String message) {
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
    }
}