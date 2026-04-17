// com/imagn/Backend/model/CloudflareConfig.java
package com.imagn.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudflareConfig {
    private String worker_url;
    private String secret;
}