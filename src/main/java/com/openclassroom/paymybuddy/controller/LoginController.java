package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.exception.UserNotFoundException;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class LoginController {

    @Autowired
    private IUserService userService;

    public LoginController (IUserService userService){
        this.userService = userService;
    }

    @GetMapping("/user")
    public String getUser(Authentication authentication) {
        String email = authentication.getName();;

        User user = userService.getUserByEmail(email).
                orElseThrow(() -> new UserNotFoundException("User not found"));

        return "Welcome " + user.getUsername();
    }
}
