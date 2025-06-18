package com.connect.controller;

// Room associate handler functions are found here.

import com.connect.dto.RoomDTO;
import com.connect.model.Room;
import com.connect.service.ChatUserService;
import com.connect.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ChatRoomController {

    @Autowired
    private ChatUserService chatUserService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Route for handling the joining message.
    @MessageMapping("/chat.join")
    public void joinRoom(@Payload Map<String, String> joinReq) {
        String username = joinReq.get("username");
        String roomId = joinReq.get("roomId");
        if (username.isEmpty() || roomId.isEmpty()) {
            log.error("Bad Request | Data not found");
            return;
        }
        chatUserService.joiningRequestHandler(username, roomId);
    }

    // Handler when the user creates a new room.
    // Person who is creating the room must give the necessary information as in body.
    @PostMapping("/room/create")
    public ResponseEntity<String> createRoom(@RequestBody  Room room, Principal principal) {
        // Room name is been given by the owner.
        // Here we derive the name of the user who is request with the help of principal.
        Room createdRoom = roomService.addNewRoom(room, principal);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Room created successfully");
    }

    @GetMapping("/room/getall")
    public ResponseEntity<List<RoomDTO>> fetchRooms() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(roomService.getRooms()
                        .stream()
                        .map(RoomDTO::new)
                        .collect(Collectors.toList()));
    }
}
