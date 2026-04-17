package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.dto.UpdatePasswordRequestDto;
import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.exception.FriendAlreadyAddedException;
import com.openclassroom.paymybuddy.exception.InvalidOperationException;
import com.openclassroom.paymybuddy.exception.UserAlreadyExistsException;
import com.openclassroom.paymybuddy.exception.UserNotFoundException;
import com.openclassroom.paymybuddy.mapper.UserMapper;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//Ajout de la javadoc ?
//Ajout de logger info, error, debug ?
@Transactional
@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;


    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserWithFriends(String email) {
        return userRepository.findByEmailWithFriends(email).
                orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserResponseDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toDto(user);
    }

    public List<User> getFriendUsernames(String email) {
        User user = userRepository.findByEmailWithFriends(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return user.getFriends();
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

        user.getFriends().add(friend);

        userRepository.save(user);
    }

    public void updatePassword(String email, UpdatePasswordRequestDto requestDto) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidOperationException("Incorrect password");
        }

        if (requestDto.getNewPassword() == null || requestDto.getNewPassword().isBlank()) {
            throw new InvalidOperationException("New password cannot be empty");
        }

        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new InvalidOperationException("New password does not match the confirmation");
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
            throw new InvalidOperationException("New password is the same as your current password");
        }

        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

}
