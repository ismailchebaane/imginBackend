package com.imagn.Backend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private String id;
    private String chatId;
    private String imageUrl;
    private String thumbnailUrl;
    private String prompt;
    private Integer width;
    private Integer height;
    private String model;
    private LocalDateTime createdAt;
}