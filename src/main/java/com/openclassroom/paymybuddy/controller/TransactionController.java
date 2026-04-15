package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.service.ITransactionService;
import com.openclassroom.paymybuddy.service.IUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TransactionController {

    private final ITransactionService transactionService;

    private final IUserService userService;

    public TransactionController(ITransactionService transactionService, IUserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping("/transactions/add")
    public String addTransaction(
            @ModelAttribute TransactionRequestDto requestDto,
            Authentication authentication, RedirectAttributes redirectAttributes) {

        String senderEmail = authentication.getName();
        transactionService.addTransaction(senderEmail, requestDto);

        redirectAttributes.addFlashAttribute("successMessage", "Transaction created");

        return "redirect:/transactions";
    }

    @GetMapping("/transactions")
    public String getTransactions(Model model, Authentication authentication) {

        String email = authentication.getName();

        List<TransactionResponseDto> transactions = transactionService.getUserTransactions(email);
        List<String> friends = userService.getFriendUsernames(email);

        model.addAttribute("transactions", transactions);
        model.addAttribute("friends", friends);

        return "transactions";
    }
}
