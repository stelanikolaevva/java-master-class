package com.stela.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> findUserById(UUID id) {
        return userDao.findUserById(id);
    }

    public List<User> getAllUsers() {
        return userDao.getUsers();
    }
}
