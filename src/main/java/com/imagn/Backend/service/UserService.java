package com.imagn.Backend.service;


import com.imagn.Backend.dto.request.ApiConfigRequest;
import com.imagn.Backend.model.ApiConfig;
import com.imagn.Backend.dto.response.UserResponse;
import com.imagn.Backend.model.User;
import com.imagn.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.imagn.Backend.dto.request.UpdateUserRequest;
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }



    public UserResponse updateUser(String currentEmail, UpdateUserRequest request) {

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // update username
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        // update email
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }

        // update password (SECURE)
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        return mapToUserResponse(user);
    }




    // NEW: Update API configurations
    public UserResponse updateApiConfigs(ApiConfigRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ApiConfig apiConfig = ApiConfig.builder()
                .pollinations(request.getPollinations())
                .huggingface(request.getHuggingface())
                .stableHorde(request.getStableHorde())
                .cloudflare(request.getCloudflare())
                .build();

        user.setApiConfigs(apiConfig);
        userRepository.save(user);

        return mapToUserResponse(user);
    }

    // NEW: Get API configurations
    public ApiConfig getApiConfigs() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getApiConfigs() != null ? user.getApiConfigs() : new ApiConfig();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .credits(user.getCredits())
                .createdAt(user.getCreatedAt())
                .apiConfigs(user.getApiConfigs())
                .build();
    }



}