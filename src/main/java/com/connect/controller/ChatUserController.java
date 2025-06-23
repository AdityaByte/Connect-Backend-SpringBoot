package com.connect.controller;

import com.connect.dto.MessageDTO;
import com.connect.dto.UserDTO;
import com.connect.model.Message;
import com.connect.model.User;
import com.connect.service.ChatUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @MessageMapping("/greet")
    public void handleGreeting(SimpMessageHeaderAccessor headerAccessor) {
        log.info("Greeting Request from the user");
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        chatUserService.greetingHandler(token);
        messagingTemplate.convertAndSend("/topic/greet", "websocket connection established");
    }

    @MessageMapping("/chat.send")
    public void handleMessage(@Payload MessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        // Fetching the RoomID from the header.
        String roomID = headerAccessor.getFirstNativeHeader("roomId");
        log.info("Sending the message to the room: {}", roomID);
        chatUserService.publishMessageToKafka(message, roomID);
        messagingTemplate.convertAndSend("/topic/chat/" + roomID, message);
    }

    // Route for handling the history.
    @MessageMapping("/chat.history")
    public void handleHistory(SimpMessageHeaderAccessor headerAccessor) {
        String roomId = headerAccessor.getFirstNativeHeader("roomId");
        log.info("Handling the History of chats for the Room: {}", roomId);

        chatUserService.chatHistoryHandler(roomId)
                .ifPresent(msgs -> {
                    log.info("Message Size: {}", msgs.size());
                    List<MessageDTO> dtoList = msgs.stream()
                            .map(MessageDTO::new)
                            .toList();
                    messagingTemplate.convertAndSend("/topic/history/"+roomId, dtoList);
                });
    }

    @MessageMapping("/users")
    public void fetchUsers() {
        chatUserService.fetchUser()
                .ifPresent(users1 -> {
                    List<UserDTO> modifiedUsers = users1
                            .stream()
                            .map(UserDTO::new)
                            .collect(Collectors.toList());
                    messagingTemplate.convertAndSend("/topic/users", modifiedUsers);
                });
    }
}