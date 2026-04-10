package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.TransactionRepository;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionService implements ITransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;


    public Transaction addTransaction(String senderEmail, String receiverEmail, String description, double amount) {
        User sender = userRepository.findByEmail(senderEmail).orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByEmail(receiverEmail).orElseThrow(() -> new RuntimeException("Receiver not found"));

        if(amount <= 0 ) {
            throw new RuntimeException("Amount must be superior to 0");
        }
        if(sender.getId() == receiver.getId()) {
            throw new RuntimeException("Sender and receiver must be different");
        }

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setDateTransaction(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getUserTransactions(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> sent = transactionRepository.findBySender(user);
        List<Transaction> received = transactionRepository.findByReceiver(user);

        List<Transaction> all = new ArrayList<>();
        all.addAll(sent);
        all.addAll(received);

        return all;
    }
}
