package com.connect.controller;

// Room associate handler functions are found here.

import com.connect.dto.RoomDTO;
import com.connect.model.Room;
import com.connect.service.ChatUserService;
import com.connect.service.JwtTokenService;
import com.connect.service.RoomService;
import com.connect.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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
    private JwtTokenService jwtTokenService;

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
    public ResponseEntity<Map<String, String>> createRoom(@RequestBody Room room, HttpServletRequest request) {
        log.info("New Room request");
        String rawToken = request.getHeader("Authorization");
        // Checking the token is valid or not.
        // This service extracts the username till then the token is valid otherwise it will not.
        String token = rawToken.substring(7);
        String username = jwtTokenService.extractUsername(token);

        if (username.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("response", "Invalid Token"));
        }
        // Here we need to send the data to the service class.
        var createdRoom = roomService.addNewRoom(room, username);
        if (createdRoom == null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("response", "Something went wrong at the server! Try again later."));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("response", createdRoom.getRoomId().toString()));
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
