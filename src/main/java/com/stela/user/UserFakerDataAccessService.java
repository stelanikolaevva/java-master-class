package com.stela.user;

import com.github.javafaker.Faker;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserFakerDataAccessService implements UserDao {
    private static final int USERS_COUNT = 20;
    private final List<User> users;

    public UserFakerDataAccessService() {
        Faker faker = new Faker();
        this.users = IntStream.range(0, USERS_COUNT)
                .mapToObj(i -> new User(UUID.randomUUID(),
                        faker.name().fullName())
                ).collect(Collectors.toList());
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
