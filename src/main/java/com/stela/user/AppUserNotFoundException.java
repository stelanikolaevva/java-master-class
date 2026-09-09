package com.stela.user;

import java.util.UUID;

public class AppUserNotFoundException extends RuntimeException {

    public AppUserNotFoundException(String message, UUID appUserId) {
        super(String.format(message, appUserId));
    }
}
