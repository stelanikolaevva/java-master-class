package com.stela.user;

import java.util.Optional;
import java.util.UUID;

public class UserDao {

    private static final User[] users;

    //Pre-seed with data
    static {
        users = new User[]{
                new User(UUID.fromString("28d59322-2f01-4794-9f07-fae11a1a7a03"), "Leo"),
                new User(UUID.fromString("a10309f4-186b-4b2c-abb6-93f9bee4dba1"), "Michael"),
                new User(UUID.fromString("b0bd60d0-619a-4c40-9a1a-861ab568a6a6"), "Amaya"),
                new User(UUID.fromString("37f04d98-9c09-495b-870f-52b322b676dc"), "Boyan"),
        };
    }

    /**
     * @return an array of all users
     */
    public User[] getUsers() {
        return users;
    }

    /**
     * @param userId of the user
     * @return an Optional with the user, or empty if none
     */
    public Optional<User> findUserById(UUID userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
