package com.rustambek.payment.service.user;

import com.rustambek.payment.dto.auth.SignInRequest;
import com.rustambek.payment.dto.auth.SignUpRequest;
import com.rustambek.payment.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {
    ResponseEntity<?> signIn(SignInRequest request);
    ResponseEntity<?> signUp(SignUpRequest request);
    User findByUsername(String username);
    ResponseEntity<?> delete(UUID id);
    ResponseEntity<?> pageable(Pageable pageable);
    ResponseEntity<?> update(UUID id, SignUpRequest requestDto);
    ResponseEntity<?> create(SignUpRequest requestDto);
    ResponseEntity<?> getById(UUID id);
    void saveViaViod(User user);
    User getCurrentUser();


}
