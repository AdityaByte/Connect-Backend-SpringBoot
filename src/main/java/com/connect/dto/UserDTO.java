package com.connect.dto;

import com.connect.enums.UserStatus;
import com.connect.model.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private UserStatus status;

    public UserDTO(User user) {
        this.id = user.getId().toString();
        this.username = user.getUsername();
        this.status = user.getStatus();
    }
}
