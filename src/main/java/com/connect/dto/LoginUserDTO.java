package com.connect.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class LoginUserDTO {
    private String email;
    private String password;
}
