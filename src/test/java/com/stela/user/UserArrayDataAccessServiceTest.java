package com.stela.user;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserArrayDataAccessServiceTest {

    private final UserArrayDataAccessService userDao = new UserArrayDataAccessService();

    @Test
    void shouldReturnAllPreSeededUsers() {
        // when
        List<User> actual = userDao.getUsers();

        // then
        assertThat(actual)
                .hasSize(4)
                .extracting(User::getName)
                .containsExactlyInAnyOrder("Leo", "Michael", "Amaya", "Boyan");
    }

    @Test
    void shouldFindUserByIdWhenUserExists() {
        // given
        UUID existingUserId = UUID.fromString("28d59322-2f01-4794-9f07-fae11a1a7a03");

        // when
        Optional<User> actual = userDao.findUserById(existingUserId);

        // then
        assertThat(actual)
                .isPresent()
                .get()
                .extracting(User::getName)
                .isEqualTo("Leo");
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserIdIsUnknown() {
        // when
        Optional<User> actual = userDao.findUserById(UUID.randomUUID());

        // then
        assertThat(actual).isEmpty();
    }
}