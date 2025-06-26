package com.connect.service;

import com.connect.model.Room;
import com.connect.model.User;
import com.connect.repository.RoomRepository;
import com.connect.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @PostConstruct
    public void init() {
        try {
            final String ROOM_NAME = "general";
            roomRepository.findRoomByName(ROOM_NAME)
                    .or(() -> {
                        // If not found, create and try to insert a new room.
                        Room generalRoom = Room.builder()
                                .roomName(ROOM_NAME)
                                .roomDescription("A public space for all users to chat, ask questions, and share updates.")
                                .timeStamp(LocalDateTime.now())
                                .build();
                        return roomRepository.addRoom(generalRoom)
                                .or(() -> {
                                    log.error("Failed to create the general room");
                                    return Optional.empty();
                                });
                    });
        } catch (Exception e) {
            log.error("Exception during roomService initialization {}", e.getMessage());
        }
    }

    public void addUserToRoom(User user, String roomId) {
        roomRepository.findRoomByID(roomId)
                .ifPresentOrElse(
                        room -> {
                            // May do it later.
                        },
                        () -> log.error("Failed to find the room")
                );
    }

    // Method for adding a new Room
    public Room addNewRoom(Room room, String username) {

        return userRepository.findByUsername(username)
                .map(user -> {
                    room.setAdmin(user);
                    return roomRepository.addRoom(room)
                            .orElseGet(() -> {
                                log.error("Failed to create the room");
                                return null;
                            });
                })
                .orElseGet(() -> {
                    log.error("No user found in database, failed to create the room");
                    return null;
                });
    }

    public List<Room> getRooms() {
        return roomRepository.getRooms()
                .orElseGet(() -> {
                    log.info("No rooms found");
                    return null;
                });
    }
}
