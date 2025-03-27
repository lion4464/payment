package com.rustambek.payment.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    @NotNull(message = "username required")
    private String username;
    @NotNull(message = "password required")
    private String password;
}
