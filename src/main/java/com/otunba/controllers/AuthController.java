package com.otunba.controllers;

import com.otunba.dtos.AppUserDto;
import com.otunba.dtos.AuthRequest;
import com.otunba.dtos.AuthResponse;
import com.otunba.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/buzz")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AppUserDto> register(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.registerUser(authRequest));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        var savedUser = authService.loginUser(request);
        return ResponseEntity.ok(savedUser);
    }
}
