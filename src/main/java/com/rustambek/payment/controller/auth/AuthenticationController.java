package com.rustambek.payment.controller.auth;

import com.rustambek.payment.dto.auth.SignInRequest;
import com.rustambek.payment.dto.auth.SignUpRequest;
import com.rustambek.payment.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping( "/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;


    @PostMapping("/sign_in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest signInRequest, HttpServletRequest request) {
        return userService.signIn(signInRequest,request);
    }

    @PostMapping("/sign_up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest, HttpServletRequest request){
        System.out.println(request.getRequestURL());
        return userService.signUp(signUpRequest,request);
    }
}
