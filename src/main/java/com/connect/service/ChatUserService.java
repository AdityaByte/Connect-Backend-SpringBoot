package com.connect.service;

import com.connect.dto.MessageDTO;
import com.connect.enums.UserStatus;
import com.connect.model.Message;
import com.connect.model.Room;
import com.connect.model.User;
import com.connect.repository.RoomRepository;
import com.connect.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ChatUserService {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    private Map<String, User> users = new ConcurrentHashMap<>();

    public void greetingHandler(String token) {
        // Extracting the username from the token.
        token = token.substring(7);
        String username = jwtTokenService.extractUsername(token);
        if (username.isEmpty()) {
            log.error("Invalid Token");
            return;
        }
        // Have to change the user status.
        Optional<User> user = userRepository.updateUserStatus(username, UserStatus.ACTIVE);
        user.ifPresent(value -> users.put(value.getUsername(), value));
    }

    public void joiningRequestHandler(String username, String roomId) {

        // Checking the user exists by the username in the cache or not.
        User requiredUser = users.get(username);
        if (requiredUser == null) {
            log.error("No user found");
            return;
        }

        ObjectId roomID = new ObjectId(roomId);

        // Checking the Room exists from the RoomId
        Optional<Room> requestedRoom = roomRepository.findRoomByID(roomID);
        if (requestedRoom.isEmpty()) {
            log.error("No room exists");
            return;
        }
        // If Room exists and the User is valid we have to update the room data.
        roomService.addUserToRoom(requiredUser,  roomID);
    }

    public Optional<Message> addMessagetoRoom(MessageDTO messageDTO, String roomId) {
        if (messageDTO == null) {
            log.error("Message DTO is null");
            return Optional.empty();
        } else if (messageDTO.getMessage().isEmpty() || roomId.isEmpty()) {
            log.error("Empty Data occurred");
            return Optional.empty();
        }

        ObjectId roomID = new ObjectId(roomId);

        return roomService.addMessage(messageDTO, roomID);
    }

    public Optional<List<Message>> chatHistoryHandler(String roomId) {
        if (roomId.isEmpty()) {
            log.error("Room ID is null in the chat history handler");
            return Optional.empty();
        }
        ObjectId roomID = new ObjectId(roomId);
        return roomRepository.getRoomSpecificMessages(roomID);
    }

    public Optional<List<User>> fetchUser() {
        log.info("Fetching all users from database");
        return userRepository.findAllUser();
    }

}
