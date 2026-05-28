package com.stela.user;

import java.util.UUID;

/**
 * Custom checked exception when the user is not found
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message, UUID userId) {
        super(String.format(message, userId.toString()));
    }
}
