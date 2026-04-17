package com.imagn.Backend.controller;

import com.imagn.Backend.dto.request.ApiConfigRequest;

import com.imagn.Backend.model.ApiConfig;
import com.imagn.Backend.dto.response.UserResponse;
import com.imagn.Backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.imagn.Backend.dto.request.UpdateUserRequest;
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }


    // NEW: Update user
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(
            @RequestParam String email,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(
                userService.updateUser(email, request)
        );
    }
    // NEW: Get API configs
    @GetMapping("/api-configs")
    public ResponseEntity<ApiConfig> getApiConfigs() {
        return ResponseEntity.ok(userService.getApiConfigs());
    }

    // NEW: Update API configs
    @PutMapping("/api-configs")
    public ResponseEntity<UserResponse> updateApiConfigs(@RequestBody ApiConfigRequest request) {
        return ResponseEntity.ok(userService.updateApiConfigs(request));
    }
}