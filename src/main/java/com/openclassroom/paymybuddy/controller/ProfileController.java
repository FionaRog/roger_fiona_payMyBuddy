package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.dto.UpdatePasswordRequestDto;
import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.service.IUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final IUserService userService;

    public ProfileController(IUserService userService){
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getProfile(Model model, Authentication authentication) {
        String email = authentication.getName();

        UserResponseDto userDto = userService.getUserProfile(email);

        model.addAttribute("user", userDto);

        return "profile";
    }

    @PostMapping("/profile/password")
    public String updatePassword(@ModelAttribute UpdatePasswordRequestDto requestDto, Authentication authentication, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();

        userService.updatePassword(email, requestDto);

        redirectAttributes.addFlashAttribute("successMessage", "Mot de passe mis à jour");

        return "redirect:/profile";
    }
}
