package com.openclassroom.paymybuddy.mapper;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = java.time.LocalDateTime.class)
public interface TransactionMapper {

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "sender", source = "sender")
    @Mapping(target = "receiver", source = "receiver")
    @Mapping(target = "dateTransaction", expression = "java(LocalDateTime.now())")
    Transaction toEntity(TransactionRequestDto dto, User sender, User receiver);

    @Mapping(source = "sender.username", target = "senderUsername")
    @Mapping(source = "receiver.username", target = "receiverUsername")
    TransactionResponseDto toDto(Transaction transaction);
}
