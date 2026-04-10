package com.openclassroom.paymybuddy.exception;

public class FriendAlreadyAddedException extends RuntimeException{

    public FriendAlreadyAddedException (String message) {
        super(message);
    }
}
