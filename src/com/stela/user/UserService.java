package com.stela.user;

import java.util.UUID;

public class UserService {
    private final UserDao userDao = new UserDao();

    /**
     * @param id - user id
     * @return the user entity
     */
    public User findUserById(UUID id) {
        User user = userDao.findUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User with id %s not found", id);
        }
        return user;
    }

    /**
     * @return an array of all users
     */
    public User[] getAllUsers() {
        return userDao.getUsers();
    }
}
