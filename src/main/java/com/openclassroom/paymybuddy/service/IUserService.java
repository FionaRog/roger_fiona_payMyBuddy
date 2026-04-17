package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.dto.UpdatePasswordRequestDto;
import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.model.User;

import java.util.List;


public interface IUserService {

    Iterable<User> getUsers();

    UserResponseDto getUserProfile(String email);

    User addUser (User user);

    void addFriend(String userEmail, String friendEmail);

    User getUserWithFriends(String email);

    List<User> getFriendUsernames(String email);

    void updatePassword(String email, UpdatePasswordRequestDto updatePasswordRequestDto);
}
