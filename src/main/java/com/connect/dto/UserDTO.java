package com.connect.dto;

import com.connect.enums.UserRole;
import com.connect.enums.UserStatus;
import com.connect.model.Room;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatUserDTO {
    private String username;
    private UserRole role;
    private UserStatus status;
    private boolean isOnline = false;
    private List<Room> joinedRooms = new ArrayList<>();
}
