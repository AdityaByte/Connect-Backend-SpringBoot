package com.connect.controller;

import com.connect.dto.ChatUserDTO;
import com.connect.dto.MessageDTO;
import com.connect.model.Room;
import com.connect.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class WebSocketController {
    // The client make the websocket connection sends the token to the server before the request we need
    // to validate it with the help of the interceptors ok.
    // ok just testing out the things here we have a handler just a handler which recieves the message
    // about the client username and email ok.
    // and we opens up a websocket connection with him.

    @Autowired
    private RoomService roomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    Map<String, ChatUserDTO> userData = new ConcurrentHashMap<>();

    @MessageMapping("/greet")
    public void handleFirstConn(@Payload ChatUserDTO chatUser) {
        System.out.println(chatUser.toString());
        userData.put(chatUser.getUsername(), chatUser);
        messagingTemplate.convertAndSend("/topic/greet", "websocket connection established");
    }

    // Route for handling the joining message.
    @MessageMapping("/chat.join")
    public void joinRoom(SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getFirstNativeHeader("username");
        String roomID = headerAccessor.getFirstNativeHeader("roomID");
        System.out.println("Joining request from " + username);

        ChatUserDTO user = userData.get(username);
        if (user == null) {
            System.out.println("Invalid user: User not set");
            return;
        }

        roomService.addUserToRoom(user, roomID);

    }

    @MessageMapping("/chat.send")
    public void handleMessage(@Payload MessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        // In this controller we have to send the message to the room.
        String roomId = headerAccessor.getFirstNativeHeader("roomId");
        System.out.println(roomId);
        System.out.println(message.toString());

        roomService.addMessage(message, roomId);

        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }

    // Route for handling the history.
    @MessageMapping("/chat.history")
    public void handleHistory(SimpMessageHeaderAccessor headerAccessor, Principal principal) {

        System.out.println("Inside handleHistory...");
        if (principal != null) {
            System.out.println("Principal in handleHistory: " + principal.getName());
        } else {
            System.out.println("Principal in handleHistory is NULL!"); // This is what you're seeing
        }

        String roomId = headerAccessor.getFirstNativeHeader("roomId");

        if (roomId == null  || roomId.isEmpty()) {
            System.out.println("Room id is null");
            return;
        }

        System.out.println("room id is: " + roomId);

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            System.out.println("No room exists");
            return;
        }

        List<MessageDTO> history = room.getMessages();

        if (history.isEmpty()) {
            System.out.println("No message found..");
            return;
        }

        System.out.println(history.toString());

        String username = principal.getName();

        System.out.println("extracted username in the handlehistory is " + username);

////        messagingTemplate.convertAndSendToUser(username, "/queue/history", history);
//        messagingTemplate.convertAndSendToUser(username,"/queue/history", history);
        messagingTemplate.convertAndSend("/topic/history/" + roomId, history);
    }

}