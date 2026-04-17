package com.imagn.Backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.imagn.Backend.model.ApiConfig;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String username;
    private String avatar;
    private Integer credits;
    private LocalDateTime createdAt;
    // NEW: API configurations
    private ApiConfig apiConfigs;
}