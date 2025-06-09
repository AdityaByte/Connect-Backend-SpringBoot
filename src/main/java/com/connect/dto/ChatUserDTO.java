package com.connect.dto;

import com.connect.enums.UserRole;
import com.connect.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatUserDTO {
    private String username;
    private UserRole role;
    private UserStatus status;
    private boolean isOnline = false;
}
