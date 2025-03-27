package com.rustambek.payment.dto.user;

import com.rustambek.payment.enums.user.UserRole;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String fullName;
    private UserRole role;
    private Long balance;
}
