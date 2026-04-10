package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.model.User;

import java.util.Optional;


public interface IUserService {

    Iterable<User> getUsers();

    User getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    User addUser (User user);

    void addFriend(String userEmail, String friendEmail);

    User getUserWithFriends(String email);
}
