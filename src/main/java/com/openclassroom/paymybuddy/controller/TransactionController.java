package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.service.ITransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TransactionController {

    private final ITransactionService transactionService;

    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions/add")
    public String addTransaction(@RequestParam String receiverEmail,
                                 @RequestParam(required = false) String description,
                                 @RequestParam double amount,
                                 Authentication authentication) {
        String senderEmail = authentication.getName();

        transactionService.addTransaction(senderEmail, receiverEmail, description, amount);

        return "Transaction created successfully";
    }

    @GetMapping("/transactions")
    public List<String> getTransactions(Authentication authentication) {

        String email = authentication.getName();

        List<Transaction> transactions = transactionService.getUserTransactions(email);

        return transactions.stream().
                map(t -> t.getSender().getUsername() + "->" +
                                    t.getReceiver().getUsername() + ":" +
                                    t.getAmount() + "|" +
                                    t.getDescription() + "|" +
                                    t.getDateTransaction())
                .toList();
    }
}
