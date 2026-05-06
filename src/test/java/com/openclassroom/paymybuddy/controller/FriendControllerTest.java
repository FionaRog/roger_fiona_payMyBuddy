package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.configuration.SpringSecurityConfig;
import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.service.IUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(FriendController.class)
@Import(SpringSecurityConfig.class)
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @Test
    @DisplayName("Should display friends page when user is authenticated")
    void shouldDisplayFriendsPageWhenUserIsAuthenticated() throws Exception {
        mockMvc.perform(get("/friends")
                        .with(user("test@mail.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("friends"));
    }

    @Test
    @DisplayName("Should redirect to friends when friend is added")
    void shouldRedirectToFriendsWhenFriendIsAdded() throws Exception {
        mockMvc.perform(post("/friends/add")
                        .with(csrf())
                        .with(user("test@mail.com"))
                        .param("email", "friend@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Should redirect with error message when friend service fails")
    void shouldRedirectWithErrorMessageWhenFriendServiceFails() throws Exception {
        doThrow(new BusinessException("friendNotFound", "Utilisateur introuvable"))
                .when(userService).addFriend(anyString(), anyString());

        mockMvc.perform(post("/friends/add")
                        .with(csrf())
                        .with(user("test@mail.com"))
                        .param("email", "missing@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends"))
                .andExpect(flash().attribute("errorMessage", "Utilisateur introuvable"));
    }
}
