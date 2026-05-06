package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.configuration.SpringSecurityConfig;
import com.openclassroom.paymybuddy.dto.UpdatePasswordRequestDto;
import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.service.IUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ProfileController.class)
@Import(SpringSecurityConfig.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @Test
    @DisplayName("Should display profile page when user is authenticated")
    void shouldDisplayProfilePageWhenUserIsAuthenticated() throws Exception {
        UserResponseDto user = new UserResponseDto();
        user.setEmail("test@mail.com");
        user.setUsername("Tester");

        when(userService.getUserProfile(anyString())).thenReturn(user);

        mockMvc.perform(get("/profile")
                        .with(user("test@mail.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user", "updatePasswordRequestDto"));
    }

    @Test
    @DisplayName("Should redirect to profile when password is updated")
    void shouldRedirectToProfileWhenPasswordIsUpdated() throws Exception {
        mockMvc.perform(post("/profile/password")
                        .with(csrf())
                        .with(user("test@mail.com"))
                        .flashAttr("updatePasswordRequestDto", new UpdatePasswordRequestDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Should redirect to profile when username is updated")
    void shouldRedirectToProfileWhenUsernameIsUpdated() throws Exception {
        mockMvc.perform(post("/profile/username")
                        .with(csrf())
                        .with(user("test@mail.com"))
                        .param("username", "NewUsername"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Should redirect to profile when balance is updated")
    void shouldRedirectToProfileWhenBalanceIsUpdated() throws Exception {
        mockMvc.perform(post("/profile/balance")
                        .with(csrf())
                        .with(user("test@mail.com"))
                        .param("amount", "25"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Should redirect with error message when profile service fails")
    void shouldRedirectWithErrorMessageWhenProfileServiceFails() throws Exception {
        doThrow(new BusinessException("invalidPassword", "Mot de passe incorrect"))
                .when(userService).updatePassword(anyString(), any(UpdatePasswordRequestDto.class));

        mockMvc.perform(post("/profile/password")
                        .with(csrf())
                        .with(user("test@mail.com"))
                        .flashAttr("updatePasswordRequestDto", new UpdatePasswordRequestDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attribute("errorMessage", "Mot de passe incorrect"));
    }
}
