package com.stela.user;

import java.util.UUID;

/**
 * Custom unchecked exception when the user is not found
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message, UUID userId) {
        super(String.format(message, userId));
    }
}
