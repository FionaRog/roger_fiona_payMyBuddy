package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.model.Transaction;

import java.util.List;

public interface ITransactionService {

    Transaction addTransaction(String senderEmail, String receiverEmail, String description, double amount);

    List<Transaction> getUserTransactions(String email);
}
