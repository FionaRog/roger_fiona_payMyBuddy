package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.exception.FriendAlreadyAddedException;
import com.openclassroom.paymybuddy.exception.InvalidOperationException;
import com.openclassroom.paymybuddy.exception.UserAlreadyExistsException;
import com.openclassroom.paymybuddy.exception.UserNotFoundException;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //createuser

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserWithFriends(String email) {
        return userRepository.findByEmailWithFriends(email).
                orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User addUser (User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already used");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void addFriend(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail).
                orElseThrow(() -> new UserNotFoundException("User not found"));

        User friend = userRepository.findByEmail(friendEmail).
                orElseThrow(() -> new UserNotFoundException("Friend not found"));

        if(user.getEmail().equals(friend.getEmail())) {
            throw new InvalidOperationException("You cannot add yourself");
        }

        if (userRepository.verifyRelation(user.getId(), friend.getId()) > 0) {
            throw new FriendAlreadyAddedException("Friend already in your contacts");
        }

        user.getUsers().add(friend);

        userRepository.save(user);
    }



    //public User deleteUser (User user) { return userRepository.delete(); }



}
