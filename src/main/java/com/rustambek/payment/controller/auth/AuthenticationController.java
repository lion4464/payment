package com.rustambek.payment.controller.auth;

import com.rustambek.payment.dto.auth.SignInRequest;
import com.rustambek.payment.dto.auth.SignUpRequest;
import com.rustambek.payment.service.user.UserService;
import com.rustambek.payment.service.user.UserServiceIml;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping( "/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;


    @PostMapping("/sign_in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest signInRequest) {
        return userService.signIn(signInRequest);
    }

    @PostMapping("/sign_up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest){
        return userService.signUp(signUpRequest);
    }
}
