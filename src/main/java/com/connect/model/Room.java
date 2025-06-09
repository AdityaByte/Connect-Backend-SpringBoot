package com.connect.model;

import com.connect.dto.ChatUserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Room {
    private String roomId;
    private String roomName;
    private List<ChatUserDTO> allUsers = new ArrayList<>();
    private List<ChatUserDTO> activeUsers = new ArrayList<>();
}
