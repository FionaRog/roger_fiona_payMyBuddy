package com.openclassroom.paymybuddy.exception;

public class InvalidTransactionException extends RuntimeException{

    public InvalidTransactionException (String message) {
        super(message);
    }
}
