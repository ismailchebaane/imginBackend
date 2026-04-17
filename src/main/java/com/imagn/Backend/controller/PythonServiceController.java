package com.imagn.Backend.controller;

import com.imagn.Backend.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Proxies ALL /api/chat/* and /api/gallery/* requests to the Python AI service.
 *
 * Flow:
 *   Frontend → Spring Boot (JWT validated here) → Python (email injected from token)
 *
 * Chat is in-memory in Python — nothing is stored in MongoDB for chats.
 * Gallery reads/writes happen in Python's in-memory store as well.
 */
@RestController
@RequestMapping("/api")
public class PythonServiceController {

    @Value("${python.service.url:http://localhost:5050}")
    private String pythonServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public PythonServiceController(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // ── TOKEN HELPERS ──────────────────────────────────────────────────────────

    private String getEmailFromToken(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token != null) {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
                return username;
            }
        }
        return null;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // ── CHAT ROUTES ────────────────────────────────────────────────────────────

    /** POST /api/chat — Create a new chat session */
    @PostMapping("/chat")
    public ResponseEntity<?> createChat(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) return unauthorized();
        body.put("email", email);
        return forwardPost(pythonServiceUrl + "/api/chat", body);
    }

    /** GET /api/chat — Get all chats for current user */
    @GetMapping("/chat")
    public ResponseEntity<?> getChats(HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) return unauthorized();
        String url = UriComponentsBuilder.fromHttpUrl(pythonServiceUrl + "/api/chat")
                .queryParam("email", email)
                .toUriString();
        return forwardGet(url);
    }

    /** GET /api/chat/{chatId} — Get a specific chat */
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<?> getChat(@PathVariable String chatId, HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) return unauthorized();
        String url = UriComponentsBuilder.fromHttpUrl(pythonServiceUrl + "/api/chat/" + chatId)
                .queryParam("email", email)
                .toUriString();
        return forwardGet(url);
    }

    /** DELETE /api/chat/{chatId} — Delete a specific chat */
    @DeleteMapping("/chat/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable String chatId, HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) return unauthorized();
        String url = UriComponentsBuilder.fromHttpUrl(pythonServiceUrl + "/api/chat/" + chatId)
                .queryParam("email", email)
                .toUriString();
        return forwardDelete(url);
    }

    /** DELETE /api/chat — Clear all chats for current user */
    @DeleteMapping("/chat")
    public ResponseEntity<?> clearAllChats(HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) return unauthorized();
        String url = UriComponentsBuilder.fromHttpUrl(pythonServiceUrl + "/api/chat")
                .queryParam("email", email)
                .toUriString();
        return forwardDelete(url);
    }

    /** POST /api/chat/generate — Generate images for a chat */
    @PostMapping("/chat/generate")
    public ResponseEntity<?> generateImages(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) return unauthorized();
        body.put("email", email);
        return forwardPost(pythonServiceUrl + "/api/chat/generate", body);
    }

    // ── GALLERY ROUTES ─────────────────────────────────────────────────────────

    /** GET /api/gallery — Get gallery images for current user */
    @GetMapping("/gallery")
    public ResponseEntity<?> getGallery(HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) return unauthorized();
        String url = UriComponentsBuilder.fromHttpUrl(pythonServiceUrl + "/api/gallery")
                .queryParam("email", email)
                .toUriString();
        return forwardGet(url);
    }

    /** DELETE /api/gallery/{imageId} — Delete a gallery image */
    @DeleteMapping("/gallery/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable String imageId, HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) return unauthorized();
        String url = UriComponentsBuilder.fromHttpUrl(pythonServiceUrl + "/api/gallery/" + imageId)
                .queryParam("email", email)
                .toUriString();
        return forwardDelete(url);
    }

    /** POST /api/gallery/{imageId}/favorite — Toggle favorite on a gallery image */
    @PostMapping("/gallery/{imageId}/favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable String imageId, HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) return unauthorized();
        String url = UriComponentsBuilder.fromHttpUrl(pythonServiceUrl + "/api/gallery/" + imageId + "/favorite")
                .queryParam("email", email)
                .toUriString();
        return forwardPost(url, null);
    }

    // ── HELPER METHODS ─────────────────────────────────────────────────────────

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
    }

    private ResponseEntity<?> forwardGet(String url) {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getResponseBodyAsString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    private ResponseEntity<?> forwardPost(String url, Map<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Object> response = restTemplate.postForEntity(url, entity, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getResponseBodyAsString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    private ResponseEntity<?> forwardDelete(String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getResponseBodyAsString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}