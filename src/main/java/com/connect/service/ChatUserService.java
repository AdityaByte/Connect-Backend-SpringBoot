package com.connect.service;

import com.connect.buffer.MessageBuffer;
import com.connect.dto.MessageDTO;
import com.connect.enums.UserStatus;
import com.connect.kafka.KafkaPublisherService;
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

    @Autowired
    private KafkaPublisherService kafkaPublisherService;

    @Autowired
    private MessageBuffer messageBuffer;

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

        // Checking the Room exists from the RoomId
        Optional<Room> requestedRoom = roomRepository.findRoomByID(roomId);
        if (requestedRoom.isEmpty()) {
            log.error("No room exists");
            return;
        }
        // If Room exists and the User is valid we have to update the room data.
        roomService.addUserToRoom(requiredUser,  roomId);
    }

    public void publishMessageToKafka(MessageDTO messageDTO, String roomId) {
        if (messageDTO == null) {
            log.error("Message DTO is null");
            return;
        } else if (messageDTO.getMessage().isEmpty() || roomId.isEmpty()) {
            log.error("Empty Data occurred");
            return;
        } else if (!ObjectId.isValid(roomId)) {
            log.error("Room ID is not valid");
            return;
        }
        // Room ID is valid and not null.
        Message message = Message.builder()
                .roomId(roomId)
                .sender(messageDTO.getSender())
                .message(messageDTO.getMessage())
                .timeStamp(messageDTO.getTimeStamp())
                .build();

        System.out.println(message.toString());

        kafkaPublisherService.sendEvent(message);
    }

    public Optional<List<Message>> chatHistoryHandler(String roomId) {
        if (roomId.isEmpty()) {
            log.error("Room ID is null in the chat history handler");
            return Optional.empty();
        }

        // Here we have to check the roomSpecific message is in the buffer or not.
        if (messageBuffer.size() != 0) {
            // Here we need to filter out the room specific messages if present in the buffer.
            List<Message> messageList = messageBuffer.getMessages().stream()
                    .filter(message -> message.getRoomId().equals(roomId))
                    .toList();
            if (!messageList.isEmpty()) {
                return Optional.of(messageList);
            }
        }
        // If the buffer is empty we have to fetch the messages from the db.
        return roomRepository.getRoomSpecificMessages(roomId);
    }

    public Optional<List<User>> fetchUser() {
        log.info("Fetching all users from database");
        return userRepository.findAllUser();
    }

}
