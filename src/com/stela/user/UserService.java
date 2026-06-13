package com.stela.user;

import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserDao userDao = new UserDao();

    public Optional<User> findUserById(UUID id) {
        return userDao.findUserById(id);
    }

    public User[] getAllUsers() {
        return userDao.getUsers();
    }
}
