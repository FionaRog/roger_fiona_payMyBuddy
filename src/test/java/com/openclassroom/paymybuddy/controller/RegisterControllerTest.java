package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.configuration.SpringSecurityConfig;
import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.service.IUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisterController.class)
@Import(SpringSecurityConfig.class)
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @Test
    @DisplayName("Should redirect to login when user is registered")
    void shouldRedirectToLoginWhenUserIsRegistered() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", "new@mail.com")
                        .param("username", "NewUser")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Should redirect with error message when register service fails")
    void shouldRedirectWithErrorMessageWhenRegisterServiceFails() throws Exception {
        doThrow(new BusinessException("emailConflict", "Email deja utilise"))
                .when(userService).addUser(ArgumentMatchers.any(User.class));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", "existing@mail.com")
                        .param("username", "ExistingUser")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("errorMessage", "Email deja utilise"));
    }
}
