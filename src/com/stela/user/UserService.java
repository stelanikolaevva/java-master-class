package com.stela.user;

import java.util.UUID;

public class UserService {
    private final UserDao userDao = new UserDao();

    /**
     * @param id - user id
     * @return the user entity
     * @throws UserNotFoundException if the user is not found
     */
    public User findUserById(UUID id) throws UserNotFoundException {
        return userDao.findUserById(id);
    }

    /**
     * @return an array of all users
     */
    public User[] getAllUsers() {
        return userDao.getUsers();
    }
}
