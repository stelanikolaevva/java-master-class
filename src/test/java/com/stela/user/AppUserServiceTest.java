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
class AppUserServiceTest {

    @Mock
    private AppUserRepository userDao;
    @InjectMocks
    private AppUserService appUserService;

    private final UUID userId = UUID.randomUUID();
    private final AppUser mockAppUser = new AppUser("MockUser");

    @Test
    void shouldReturnUserWhenFindById() {
        // given
        when(userDao.findById(userId)).thenReturn(Optional.of(mockAppUser));

        // when
        Optional<AppUser> actual = appUserService.findUserById(userId);

        // then
        assertThat(actual).contains(mockAppUser);
    }

    @Test
    void shouldReturnOptionEmptyWhenNoSuchUser() {
        // given
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        // when
        Optional<AppUser> actual = appUserService.findUserById(userId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnAllUsersWhenGetAllUsers() {
        // given
        when(userDao.findAll()).thenReturn(List.of(mockAppUser, mockAppUser, mockAppUser));

        // when
        List<AppUser> actual = appUserService.getAllUsers();

        // then
        assertThat(actual).containsExactly(mockAppUser, mockAppUser, mockAppUser);
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {
        // given
        when(userDao.findAll()).thenReturn(List.of());

        // when
        List<AppUser> actual = appUserService.getAllUsers();

        // then
        assertThat(actual).isEmpty();
    }
}