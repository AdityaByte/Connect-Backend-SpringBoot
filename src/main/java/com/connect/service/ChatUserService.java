package com.connect.service;

import com.connect.dto.ChatUserDTO;
import com.connect.dto.MessageDTO;
import com.connect.model.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ChatUserService {

    @Autowired
    private RoomService roomService;

    Map<String, ChatUserDTO> users = new ConcurrentHashMap<>();

    public void greetingHandler(ChatUserDTO chatUser) {
        // Checks on the Chat-User for confirming the user is valid or not.
        if (chatUser == null) {
            log.error("No chat user exists | Bad request from the client");
        }
        // Else we need to put the user in a persistent storage maybe in the db or a temporary storage.
        chatUser.setOnline(true);
        users.put(chatUser.getUsername(), chatUser);
    }

    public void joiningRequestHandler(String username, String roomID) {

        if (username.isEmpty() || roomID.isEmpty()) {
            log.error("Fields are empty in the joining request");
            return;
        }

        ChatUserDTO chatUserDTO = users.get(username);
        if (chatUserDTO == null) {
            log.error("No Chat User exists");
            return;
        }
        // Checking the Room exists from the RoomId
        Room requestedRoom = roomService.getRoom(roomID);
        if (requestedRoom == null) {
            log.error("No room exists");
            return;
        }
        // If Room exists and the User is valid we have to update the room data.
        roomService.addUserToRoom(chatUserDTO,  roomID);
    }

    public void addMessagetoRoom(MessageDTO messageDTO, String roomID) {
        if (messageDTO == null) {
            log.error("Message DTO is null");
            return;
        } else if (messageDTO.getMessage().isEmpty() || roomID.isEmpty()) {
            log.error("Empty Data occurred");
            return;
        }

        roomService.addMessage(messageDTO, roomID);
    }

    public List<MessageDTO> chatHistoryHandler(String roomID) {
        if (roomID.isEmpty()) {
            log.error("Room ID is empty in the chat history handler");
            return null;
        }
        Room requestedRoom = roomService.getRoom(roomID);
        if (requestedRoom == null) {
            log.error("No room exists");
            return null;
        }
        List<MessageDTO> messages = requestedRoom.getMessages();
        return messages;
    }

}
