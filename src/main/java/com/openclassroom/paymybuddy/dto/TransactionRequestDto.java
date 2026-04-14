package com.openclassroom.paymybuddy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequestDto {

    private String receiverEmail;
    private String description;
    private double amount;

}
