package com.imagn.Backend.repository;


import com.imagn.Backend.model.GeneratedImage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends MongoRepository<GeneratedImage, String> {
    List<GeneratedImage> findByUserIdOrderByCreatedAtDesc(String userId);
    List<GeneratedImage> findByChatId(String chatId);
}