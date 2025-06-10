package com.connect.controller;

import com.connect.dto.ChatUserDTO;
import com.connect.dto.MessageDTO;
import com.connect.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WebSocketController {
    // The client make the websocket connection sends the token to the server before the request we need
    // to validate it with the help of the interceptors ok.
    // ok just testing out the things here we have a handler just a handler which recieves the message
    // about the client username and email ok.
    // and we opens up a websocket connection with him.

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    Map<String, ChatUserDTO> userData = new HashMap<>();

    @MessageMapping("/greet")
    @SendTo("/topic/greet")
    public void handleFirstConn(@Payload ChatUserDTO chatUser) {
        System.out.println(chatUser.toString());
        userData.put(chatUser.getUsername(), chatUser);
        messagingTemplate.convertAndSend("/topic/greet", "websocket connection established");
    }

    // creating a dummy room
    Room room = new Room("room1", "welcome room", null, null);

    // Here we have a route for handling the messages and the room request ok

    @MessageMapping("/chat.join")
    public ResponseEntity<String> joinRoom(SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getFirstNativeHeader("username");
        String roomID = headerAccessor.getFirstNativeHeader("roomID");
        System.out.println("Joining request from " + username);

        ChatUserDTO data = userData.get(username);
        if (data == null) {
            System.out.println("Invalid user: User not set");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user: user not found");
        }
        // else if the user is found then in that case i have to set the joined data to the room ok.
        data.setJoinedRooms(List.of(room));
        room.setActiveUsers(List.of(data));
        room.setAllUsers(List.of(data));
        return ResponseEntity.ok("Joined the room " + roomID + " successfully");
    }

    @MessageMapping("/chat.send")
    public void handleMessage(@Payload MessageDTO message) {
        System.out.println(message.toString());
        String roomId = message.getRoomId();
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }
}