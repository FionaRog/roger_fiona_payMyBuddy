package com.openclassroom.paymybuddy.mapper;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionMapperTest {

    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    @DisplayName("Should map transaction request to transaction entity")
    void shouldMapTransactionRequestToTransactionEntity() {
        User sender = new User();

        User receiver = new User();
        receiver.setEmail("ReceiverEmail");

        TransactionRequestDto transactionRequestDto = new TransactionRequestDto();
        transactionRequestDto.setReceiverEmail("ReceiverEmail");
        transactionRequestDto.setAmount(10);
        transactionRequestDto.setDescription("Description");

        Transaction transaction = transactionMapper.toEntity(transactionRequestDto,  sender, receiver);

        assertEquals(sender, transaction.getSender());
        assertEquals(receiver, transaction.getReceiver());
        assertEquals(transactionRequestDto.getAmount(), transaction.getAmount());
        assertEquals(transactionRequestDto.getDescription(), transaction.getDescription());
        assertEquals(transactionRequestDto.getReceiverEmail(), transaction.getReceiver().getEmail());
    }

    @Test
    @DisplayName("Should map transaction to transaction response dto")
    void shouldMapTransactionToTransactionResponseDto(){
        User receiver = new User();
        receiver.setUsername("Receiver");

        User sender = new User();
        sender.setUsername("Sender");

        LocalDateTime date = LocalDateTime.of(2026,1,20,10,30);

        Transaction transaction = new Transaction();
        transaction.setReceiver(receiver);
        transaction.setSender(sender);
        transaction.setAmount(50);
        transaction.setFee(2.50);
        transaction.setDescription("desc");
        transaction.setDateTransaction(date);

        TransactionResponseDto transactionResponseDto = transactionMapper.toDto(transaction);

        assertEquals(receiver.getUsername(), transactionResponseDto.getReceiverUsername());
        assertEquals(sender.getUsername(), transactionResponseDto.getSenderUsername());
        assertEquals(transaction.getAmount(), transactionResponseDto.getAmount());
        assertEquals(transaction.getFee(), transactionResponseDto.getFee());
        assertEquals(transaction.getDescription(), transactionResponseDto.getDescription());
        assertEquals(transaction.getDateTransaction(), transactionResponseDto.getDateTransaction());
    }

}
