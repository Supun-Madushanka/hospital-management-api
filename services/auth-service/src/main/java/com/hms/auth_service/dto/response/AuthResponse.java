package com.hms.auth_service.dto.response;

import com.hms.auth_service.enums.Role;
import com.hms.auth_service.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private Long id;
    private String email;
    private Role role;
    private UserStatus status;
    private String token;
}
