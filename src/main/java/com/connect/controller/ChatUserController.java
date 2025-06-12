package com.connect.controller;

import com.connect.dto.ChatUserDTO;
import com.connect.dto.MessageDTO;
import com.connect.service.ChatUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ChatUserController
// Controller for handling the websocket routes
// Handles greeting request, joining request, History request.

@RestController
@Slf4j
public class ChatUserController {

    @Autowired
    private ChatUserService chatUserService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Temporary persistent (in memory) storage for saving the user.
    // May remove it later just for testing purpose.
    Map<String, ChatUserDTO> userData = new ConcurrentHashMap<>();

    @MessageMapping("/greet")
    public void handleGreeting(@Payload ChatUserDTO chatUser) {
        log.info("Greeting Request from the user: {}", chatUser.toString());
        chatUserService.greetingHandler(chatUser);
        messagingTemplate.convertAndSend("/topic/greet", "websocket connection established");
    }

    // Route for handling the joining message.
    @MessageMapping("/chat.join")
    public void joinRoom(SimpMessageHeaderAccessor headerAccessor) {
        String user = headerAccessor.getFirstNativeHeader("username");
        String roomID = headerAccessor.getFirstNativeHeader("roomID");
        log.info("Joining Request from user: {} and room: {}", user, roomID);
        chatUserService.joiningRequestHandler(user, roomID);
    }

    @MessageMapping("/chat.send")
    public void handleMessage(@Payload MessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        // Fetching the RoomID from the header.
        String roomID = headerAccessor.getFirstNativeHeader("roomId");
        log.info("Sending the message to the room: {}", roomID);
        chatUserService.addMessagetoRoom(message, roomID);
        messagingTemplate.convertAndSend("/topic/chat/" + roomID, message);
    }

    // Route for handling the history.
    @MessageMapping("/chat.history")
    public void handleHistory(SimpMessageHeaderAccessor headerAccessor) {
        String roomID = headerAccessor.getFirstNativeHeader("roomId");
        log.info("Handling the History of chats for the Room: {}", roomID);
        var messages = chatUserService.chatHistoryHandler(roomID);
        if (!messages.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/history/" + roomID, messages);
        }
    }

}