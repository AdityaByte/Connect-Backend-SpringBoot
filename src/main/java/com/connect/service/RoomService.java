package com.connect.service;

import com.connect.model.Room;
import com.connect.model.User;
import com.connect.repository.RoomRepository;
import com.connect.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RoomService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @PostConstruct
    public void init() {
        try {
            final String ROOM_NAME = "general";
            Optional<Room> room = roomRepository.findRoomByName(ROOM_NAME);
            if (room.isEmpty()) {
                // Have to create the first room.
                Room generalRoom = new Room(
                        ROOM_NAME,
                        "A public space for all users to chat, ask questions, and share updates.",
                        LocalDateTime.now()
                );
                Optional<Room> createdRoom = roomRepository.addRoom(generalRoom);
                if (createdRoom.isEmpty()) {
                    log.error("Failed to create the general room");
                }
            }
        } catch (Exception e) {
            log.error("Exception during roomService initialization {}", e.getMessage());
        }
    }

    public void addUserToRoom(User user, String roomId) {
        Optional<Room> room = roomRepository.findRoomByID(roomId);
        if (room.isEmpty()) {
            log.error("No room exists failed to add user to the room");
            return;
        }

    }
    
    public boolean isRoomExists(String roomId) {
        return roomRepository.findRoomByID(roomId).isPresent();
    }

    // Method for adding a new Room
    public Room addNewRoom(Room room, String username) {
        Optional<User> fetchedUser = userRepository.findByUsername(username);
        if (fetchedUser.isEmpty()) {
            log.error("No user found in the DB, failed to create new room");
            return null;
        }
        room.setAdmin(fetchedUser.get());
        Optional<Room> createdRoom = roomRepository.addRoom(room);
        if (createdRoom.isEmpty()) {
            log.error("Failed to create the room");
            return null;
        }

        return createdRoom.get();
    }

    public List<Room> getRooms() {
        Optional<List<Room>> rooms = roomRepository.getRooms();
        if (rooms.isEmpty() || rooms.get().isEmpty()) {
            log.info("No rooms found");
            return null;
        }
        return rooms.get();
    }
}
