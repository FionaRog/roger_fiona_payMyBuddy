package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.service.ITransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TransactionController {

    private final ITransactionService transactionService;

    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions/add")
    public ResponseEntity<TransactionResponseDto> addTransaction(
            @RequestBody TransactionRequestDto requestDto,
            Authentication authentication) {

        String senderEmail = authentication.getName();
        TransactionResponseDto responseDto =
                transactionService.addTransaction(senderEmail, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/transactions")
    public String getTransactions(Model model, Authentication authentication) {

        String email = authentication.getName();

        List<TransactionResponseDto> transactions = transactionService.getUserTransactions(email);

        model.addAttribute("transactions", transactions);

        return "transactions";
    }
}
