package com.connect.dto;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@ToString
public class LoginUserDTO {
    private String email;
    private String password;
}
