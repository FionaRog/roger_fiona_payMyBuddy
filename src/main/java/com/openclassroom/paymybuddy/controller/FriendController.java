package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.service.IUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FriendController {


    private final IUserService userService;

    public FriendController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/friends/add")
    public String addFriend(@RequestParam String email, Authentication authentication, RedirectAttributes redirectAttributes) {
        String currentUserEmail = authentication.getName();

        userService.addFriend(currentUserEmail, email);

        redirectAttributes.addFlashAttribute("successMessage", "Friend added");

        return "redirect:/friends";
    }

    // utile ? pas d'option de vue pour visualiser sa liste de friends
    @GetMapping("/friends")
    public String getFriends(Model model, Authentication authentication) {
        String currentUserEmail = authentication.getName();

        User user = userService.getUserWithFriends(currentUserEmail);

        List<String> friends = user.getFriends()
                .stream()
                .map(User::getUsername)
                .toList();

        model.addAttribute("friends", friends);

        return "friends";
    }
}
