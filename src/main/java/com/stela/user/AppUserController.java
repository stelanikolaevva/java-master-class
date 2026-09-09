package com.stela.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping
    public ResponseEntity<List<AppUserResponse>> getAllUsers() {
        List<AppUserResponse> responses = appUserService.getAllUsers().stream()
                .map(appUser ->
                        new AppUserResponse(appUser.getId(), appUser.getName()))
                .toList();
        return ResponseEntity.ok(responses);
    }
}
