package com.stela.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public Optional<AppUser> findUserById(UUID id) {
        return appUserRepository.findById(id);
    }

    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }
}
