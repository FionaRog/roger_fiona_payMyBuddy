package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.repository.TransactionRepository;
import com.openclassroom.paymybuddy.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransactionService implements ITransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction addTransaction(Transaction transaction) {
        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be superior to 0");
        }
        if (transaction.getSender().getId() == transaction.getReceiver().getId()) {
            throw new IllegalArgumentException("Sender and receiver must be different");
        }
        return transactionRepository.save(transaction);

    }
}
