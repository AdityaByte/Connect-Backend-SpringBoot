package com.connect.model;

import com.connect.dto.ChatUserDTO;
import com.connect.dto.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private String roomId;
    private String roomName;
    private Map<String, ChatUserDTO> allUsers; // The key is the username.
    private Map<String, ChatUserDTO> activeUsers;
    private List<MessageDTO> messages;
}
