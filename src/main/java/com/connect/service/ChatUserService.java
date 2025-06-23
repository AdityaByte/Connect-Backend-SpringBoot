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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@AllArgsConstructor
public class ChatUserService {

    private RoomService roomService;

    private RoomRepository roomRepository;

    private JwtTokenService jwtTokenService;

    private UserRepository userRepository;

    private KafkaPublisherService kafkaPublisherService;

    private MessageBuffer messageBuffer;

    private Map<String, User> users = new ConcurrentHashMap<>();

    public void greetingHandler(String token) {
        // Extracting the username from the token.

       String username = Optional.ofNullable(jwtTokenService.extractUsername(token))
               .orElseGet(() -> {
                   log.error("Invalid Token");
                   return null;
               });

       if (username == null || username.isEmpty()) {
            log.error("Failed to extract username from token");
            return;
       }

       userRepository.updateUserStatus(username, UserStatus.ACTIVE)
               .ifPresent(user -> users.put(user.getUsername(), user));
    }

    public void joiningRequestHandler(String username, String roomId) {

        // Checking the user exists by the username in the cache or not.

        User requiredUser = Optional.ofNullable(users.get(username))
                .orElseGet(() -> {
                    log.error("No user found");
                    return null;
                });

        if (requiredUser == null) {
            log.error("No user exists in the temporary database");
            return;
        }

        // Checking the Room exists from the RoomId
        roomRepository.findRoomByID(roomId).ifPresentOrElse(room -> {
                roomService.addUserToRoom(requiredUser, room.getRoomId());
                log.info("User '{}' added to room '{}'", username, roomId);
            },
            () -> log.error("No room found in the database with roomId: {}", roomId)
        );
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

        log.info("Message: {}", message.toString());

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

    @Cacheable(value = "userCache")
    public Optional<List<User>> fetchUser() {
        log.info("Fetching all users from database");
        return userRepository.findAllUser();
    }

}
