package com.stela.user;

import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserDao userDao = new UserDao();

    /**
     * @param id - user id
     * @return the user entity
     */
    public User findUserById(UUID id) {
        Optional<User> user = userDao.findUserById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id %s not found", id);
        }
        return user.get();
    }

    /**
     * @return an array of all users
     */
    public User[] getAllUsers() {
        return userDao.getUsers();
    }
}
