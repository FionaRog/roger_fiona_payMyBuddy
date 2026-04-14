package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;

import java.util.List;

public interface ITransactionService {

    TransactionResponseDto addTransaction(String senderEmail, TransactionRequestDto requestDto);

    List<TransactionResponseDto> getUserTransactions(String email);
}
