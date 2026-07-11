package com.stela.user;

import java.util.Optional;
import java.util.UUID;

public interface UserDao {

    User[] getUsers();

    Optional<User> findUserById(UUID userId);
}
