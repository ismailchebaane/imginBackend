package com.imagn.Backend.controller;

import com.imagn.Backend.dto.request.LoginRequest;
import com.imagn.Backend.dto.request.RegisterRequest;
import com.imagn.Backend.dto.response.AuthResponse;
import com.imagn.Backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        System.out.println(request);
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        System.out.println(request);
        return ResponseEntity.ok(authService.login(request));
    }
}