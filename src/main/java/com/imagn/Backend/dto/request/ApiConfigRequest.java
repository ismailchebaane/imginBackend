package com.imagn.Backend.dto.request;

import lombok.Data;
import com.imagn.Backend.model.CloudflareConfig;

@Data
public class ApiConfigRequest {
    private String pollinations;
    private String huggingface;
    private String stableHorde;
    private CloudflareConfig cloudflare; // ← was String
}