package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FriendController {

    @Autowired
    private IUserService userService;

    public FriendController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/friends")
    public String addFriend(@RequestParam String email, Authentication authentication) {
        String currentUserEmail = authentication.getName();

        userService.addFriend(currentUserEmail, email);

        return "Friend added";
    }

    @GetMapping("/friends")
    public List<String> getFriends(Authentication authentication) {
        String currentUserEmail = authentication.getName();;

        User user = userService.getUserWithFriends(currentUserEmail);

        return user.getUsers()
                .stream()
                .map(User::getUsername)
                .toList();
    }
}
