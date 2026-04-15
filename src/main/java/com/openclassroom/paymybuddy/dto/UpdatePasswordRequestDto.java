package com.openclassroom.paymybuddy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequestDto {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
