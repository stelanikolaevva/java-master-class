package com.stela.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserArrayDataAccessService implements UserDao {

    private static final List<User> users;

    //Pre-seed with data
    static {
        users = List.of(
                new User(UUID.fromString("28d59322-2f01-4794-9f07-fae11a1a7a03"), "Leo"),
                new User(UUID.fromString("a10309f4-186b-4b2c-abb6-93f9bee4dba1"), "Michael"),
                new User(UUID.fromString("b0bd60d0-619a-4c40-9a1a-861ab568a6a6"), "Amaya"),
                new User(UUID.fromString("37f04d98-9c09-495b-870f-52b322b676dc"), "Boyan"));
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public Optional<User> findUserById(UUID userId) {
        return users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }
}
