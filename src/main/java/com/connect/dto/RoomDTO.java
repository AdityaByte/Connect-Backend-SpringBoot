package com.connect.dto;

import com.connect.model.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private String roomId;
    private String roomName;
    private String roomDescription;
    private String admin;

    public RoomDTO(Room room) {
        this.roomId = room.getRoomId().toString();
        this.roomName = room.getRoomName();
        this.roomDescription = room.getRoomDescription();
        if (room.getAdmin() != null) {
            this.admin = room.getAdmin().getUsername();
        }
    }
}
