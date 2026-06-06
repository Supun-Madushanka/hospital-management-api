package com.hms.user_service.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @PostMapping("/api/auth/register")
    AuthResponse register(@RequestBody AuthRequest request);

    @PutMapping("/api/auth/update-status/{authId}")
    AuthResponse updateStatus(@PathVariable("authId") Long authId,
                              @RequestBody StatusUpdateRequest request);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class AuthRequest {
        private String email;
        private String password;
        private String role;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class StatusUpdateRequest {
        private String status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class AuthResponse {
        private boolean success;
        private String message;
        private AuthData data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class AuthData {
        private Long id;
        private String email;
        private String role;
        private String status;
        private String token;
    }
}