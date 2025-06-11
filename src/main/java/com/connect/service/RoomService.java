package com.connect.service;

import com.connect.dto.ChatUserDTO;
import com.connect.dto.MessageDTO;
import com.connect.model.Room;
import jakarta.mail.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public RoomService() {
        Room room = new Room("general", "General Room", new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ArrayList<>());
        rooms.put(room.getRoomId(), room);
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void addUserToRoom(ChatUserDTO user, String roomId) {
        Room room = getRoom(roomId);
        if (room == null) {
            System.out.println("No room exists");
            return;
        }
        // else we need to put the user to the room.
        room.getAllUsers().putIfAbsent(user.getUsername(), user);
    }

    public void addMessage(MessageDTO messageDTO, String roomId) {
        Room room = getRoom(roomId);
        if (room == null) {
            System.out.println("No room exists");
            return;
        }
        // else we have to put down the message to the room.
        room.getMessages().add(messageDTO);
    }

    public List<MessageDTO> getMessages(String roomId) {
        Room room = getRoom(roomId);
        if (room == null) {
            System.out.println("No room exists");
            return null;
        }
        return room.getMessages();
    }
    
    public boolean isRoomExists(String roomId) {
        return rooms.containsKey(roomId);
    }
}
