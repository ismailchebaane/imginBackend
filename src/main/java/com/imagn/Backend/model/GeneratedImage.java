package com.imagn.Backend.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "generated_images")
public class GeneratedImage {

    @Id
    private String id;
    private String chatId;
    private String userId;
    private String prompt;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer width;
    private Integer height;
    private String model;
    private String status;

    @CreatedDate
    private LocalDateTime createdAt;
}