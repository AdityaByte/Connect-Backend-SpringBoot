package com.connect.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String sender;
    private String message;
}
