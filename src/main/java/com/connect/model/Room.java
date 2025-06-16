package com.connect.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rooms")
public class Room {
    @Id
    private ObjectId roomId;
    private String roomName;
    private String roomDescription;
    private LocalDateTime timeStamp;
    @DBRef
    private List<User> moderators;
    @DBRef
    private User admin;
    @DBRef
    private List<User> coAdmin;
    @DBRef
    private List<User> allUsers; // The key is the username.

    public Room(String roomName, String roomDescription, User admin) {
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.timeStamp = LocalDateTime.now();
        this.admin = admin;
    }
}