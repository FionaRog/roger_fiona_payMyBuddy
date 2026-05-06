package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.configuration.SpringSecurityConfig;
import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.service.ITransactionService;
import com.openclassroom.paymybuddy.service.IUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(SpringSecurityConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ITransactionService transactionService;

    @MockitoBean
    private IUserService userService;

    @Test
    @DisplayName("Should display transactions page when user is authenticated")
    void shouldDisplayTransactionsPageWhenUserIsAuthenticated() throws Exception {
        UserResponseDto mockUser = new UserResponseDto();
        mockUser.setUsername("Tester");

        when(userService.getUserProfile(anyString())).thenReturn(mockUser);
        when(userService.getFriendUsernames(anyString())).thenReturn(new ArrayList<>());
        when(transactionService.getUserTransactions(anyString())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/transactions")
                        .with(user("test@mail.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions"))
                .andExpect(model().attributeExists("transactions", "friends", "transactionRequestDto"));
    }

    @Test
    @DisplayName("Should redirect to transactions when transaction is added")
    void shouldRedirectToTransactionsWhenTransactionIsAdded() throws Exception {
        mockMvc.perform(post("/transactions/add")
                        .with(csrf())
                        .with(user("test@mail.com"))
                        .flashAttr("transactionRequestDto", new TransactionRequestDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Should return sent transactions when filter is sent")
    void shouldReturnSentTransactionsWhenFilterIsSent() throws Exception {
        UserResponseDto mockUser = new UserResponseDto();
        mockUser.setUsername("Sender");

        TransactionResponseDto sentTransaction = new TransactionResponseDto();
        sentTransaction.setSenderUsername("Sender");

        when(userService.getUserProfile(anyString())).thenReturn(mockUser);
        when(transactionService.getUserTransactions(anyString())).thenReturn(List.of(sentTransaction));

        mockMvc.perform(get("/transactions").param("filter", "sent")
                        .with(user("test@mail.com")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedFilter", "sent"))
                .andExpect(model().attribute("transactions", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    @DisplayName("Should return received transactions when filter is received")
    void shouldReturnReceivedTransactionsWhenFilterIsReceived() throws Exception {
        UserResponseDto mockUser = new UserResponseDto();
        mockUser.setUsername("receiver");

        TransactionResponseDto receivedTransaction = new TransactionResponseDto();
        receivedTransaction.setReceiverUsername("receiver");

        when(userService.getUserProfile(anyString())).thenReturn(mockUser);
        when(transactionService.getUserTransactions(anyString())).thenReturn(List.of(receivedTransaction));

        mockMvc.perform(get("/transactions").param("filter", "received")
                        .with(user("test@mail.com")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedFilter", "received"))
                .andExpect(model().attribute("transactions", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    @DisplayName("Should redirect with error message when transaction service fails")
    void shouldRedirectWithErrorMessageWhenTransactionServiceFails() throws Exception {
        doThrow(new BusinessException("errorMessage", "Solde insuffisant"))
                .when(transactionService).addTransaction(anyString(), ArgumentMatchers.any(TransactionRequestDto.class));

        mockMvc.perform(post("/transactions/add").with(csrf())
                        .with(user("test@mail.com"))
                        .flashAttr("transactionRequestDto", new TransactionRequestDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"))
                .andExpect(flash().attribute("errorMessage", "Solde insuffisant"));
    }
}
