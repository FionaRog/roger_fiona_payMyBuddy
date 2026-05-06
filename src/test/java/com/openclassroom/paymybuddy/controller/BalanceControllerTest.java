package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.configuration.SpringSecurityConfig;
import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.service.IBalanceService;
import com.openclassroom.paymybuddy.service.IUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
@Import(SpringSecurityConfig.class)
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IBalanceService balanceService;

    @MockitoBean
    private IUserService userService;

    @Test
    @DisplayName("Should redirect to profile when deposit is successful")
    void shouldRedirectToProfileWhenDepositIsSuccessful() throws Exception {
        mockMvc.perform(post("/profile/deposit")
                        .with(csrf())
                        .with(user("test@mail.com"))
                        .param("amount", "50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Should redirect to profile when withdraw is successful")
    void shouldRedirectToProfileWhenWithdrawIsSuccessful() throws Exception {
        mockMvc.perform(post("/profile/withdraw")
                        .with(csrf())
                        .with(user("test@mail.com"))
                        .param("amount", "20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Should redirect with error message when balance service fails")
    void shouldRedirectWithErrorMessageWhenBalanceServiceFails() throws Exception {
        doThrow(new BusinessException("invalidAmount", "Montant invalide"))
                .when(balanceService).deposit(anyString(), anyDouble());

        mockMvc.perform(post("/profile/deposit")
                        .with(csrf())
                        .with(user("test@mail.com"))
                        .param("amount", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attribute("errorMessage", "Montant invalide"));
    }
}
