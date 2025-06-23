package com.connect.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OtpDTO {
    private String otp;
    private String email;
}
