package com.imagn.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiConfig {
    private String pollinations;
    private String huggingface;
    private String stableHorde;
    private CloudflareConfig cloudflare;


}