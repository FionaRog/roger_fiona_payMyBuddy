package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.exception.InvalidTransactionException;
import com.openclassroom.paymybuddy.exception.UserNotFoundException;
import com.openclassroom.paymybuddy.mapper.TransactionMapper;
import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.TransactionRepository;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionService implements ITransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionMapper transactionMapper;


    public TransactionResponseDto addTransaction(String senderEmail, TransactionRequestDto requestDto) {
        User sender = userRepository.findByEmail(senderEmail).
                orElseThrow(() -> new UserNotFoundException("Sender not found"));

        User receiver = userRepository.findByEmail(requestDto.getReceiverEmail()).
                orElseThrow(() -> new UserNotFoundException("Receiver not found"));

        if(requestDto.getAmount() <= 0 ) {
            throw new InvalidTransactionException("Amount must be superior to 0");
        }
        if(sender.getId() == receiver.getId()) {
            throw new InvalidTransactionException("Sender and receiver must be different");
        }

        Transaction transaction = transactionMapper.toEntity(requestDto, sender, receiver);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toDto(savedTransaction);
    }

    public List<TransactionResponseDto> getUserTransactions(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Transaction> sent = transactionRepository.findBySender(user);
        List<Transaction> received = transactionRepository.findByReceiver(user);

        List<Transaction> all = new ArrayList<>();
        all.addAll(sent);
        all.addAll(received);

        return all.stream()
                .map(transactionMapper::toDto)
                .toList();
    }
}
