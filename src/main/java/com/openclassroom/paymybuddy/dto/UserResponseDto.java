package com.openclassroom.paymybuddy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    private String username;
    private String email;
    private double balance;

}
