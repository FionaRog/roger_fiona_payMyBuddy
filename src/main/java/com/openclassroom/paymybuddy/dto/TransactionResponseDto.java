package com.openclassroom.paymybuddy.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponseDto {

    private int id;
    private String senderUsername;
    private String receiverUsername;
    private String description;
    private double amount;
    private LocalDateTime dateTransaction;

}
