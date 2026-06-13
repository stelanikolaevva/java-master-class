package com.stela.user;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message, UUID userId) {
        super(String.format(message, userId));
    }
}
