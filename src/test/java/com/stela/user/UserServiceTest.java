package com.stela.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserService userService;

    private final UUID userId = UUID.randomUUID();
    private final User mockUser = new User(userId, "MockUser");

    @Test
    void shouldReturnUserWhenFindById() {
        // given
        when(userDao.findUserById(userId)).thenReturn(Optional.of(mockUser));

        // when
        Optional<User> actual = userService.findUserById(userId);

        // then
        assertThat(actual).contains(mockUser);
    }

    @Test
    void shouldReturnOptionEmptyWhenNoSuchUser() {
        // given
        when(userDao.findUserById(userId)).thenReturn(Optional.empty());

        // when
        Optional<User> actual = userService.findUserById(userId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnAllUsersWhenGetAllUsers() {
        // given
        when(userDao.getUsers()).thenReturn(List.of(mockUser, mockUser, mockUser));

        // when
        List<User> actual = userService.getAllUsers();

        // then
        assertThat(actual).containsExactly(mockUser, mockUser, mockUser);
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {
        // given
        when(userDao.getUsers()).thenReturn(List.of());

        // when
        List<User> actual = userService.getAllUsers();

        // then
        assertThat(actual).isEmpty();
    }
}