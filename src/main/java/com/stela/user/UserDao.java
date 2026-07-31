package com.stela.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDao {

    List<User> getUsers();

    Optional<User> findUserById(UUID userId);
}
