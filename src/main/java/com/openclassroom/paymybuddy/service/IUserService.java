package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.dto.UpdatePasswordRequestDto;
import com.openclassroom.paymybuddy.model.User;


public interface IUserService {

    Iterable<User> getUsers();

    User getUserByEmail(String email);

    User addUser (User user);

    void addFriend(String userEmail, String friendEmail);

    User getUserWithFriends(String email);

    void updatePassword(String email, UpdatePasswordRequestDto updatePasswordRequestDto);
}
