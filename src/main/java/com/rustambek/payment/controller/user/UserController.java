package com.rustambek.payment.controller.user;

import com.rustambek.payment.dto.auth.SignUpRequest;
import com.rustambek.payment.dto.transactions.TransactionRequest;
import com.rustambek.payment.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping( "/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SignUpRequest requestDto, HttpServletRequest request) {
        return userService.create(requestDto, request);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        return userService.getById(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody SignUpRequest requestDto) {
        return userService.update(id,requestDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return userService.delete(id);
    }
    @GetMapping
    public ResponseEntity<?> getUsers(Pageable pageable) {
        return userService.pageable(pageable);
    }

}
