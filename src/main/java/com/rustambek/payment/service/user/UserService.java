package com.rustambek.payment.service.user;

import com.rustambek.payment.config.TokenUtil;
import com.rustambek.payment.config.UserDetailsImpl;
import com.rustambek.payment.dto.auth.AuthResponse;
import com.rustambek.payment.dto.auth.SignInRequest;
import com.rustambek.payment.dto.auth.SignUpRequest;
import com.rustambek.payment.exception.RecordForbiddenException;
import com.rustambek.payment.exception.UserAlreadyExistException;
import com.rustambek.payment.model.user.User;
import com.rustambek.payment.model.user.UserRole;
import com.rustambek.payment.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RecordForbiddenException("User with username: " + username + " not found"));
    }

    public ResponseEntity<?> signIn(SignInRequest signInRequest, HttpServletRequest request) {
        Optional<User> optional = findFirstByUsername(signInRequest.getUsername());
        if (optional.isPresent()) {
            if (!passwordEncoder.matches(signInRequest.getPassword(), optional.get().getPassword())) {
                throw new BadCredentialsException(signInRequest.getUsername());
            }
        } else {
            throw new BadCredentialsException(signInRequest.getUsername());
        }
        return createAuthResponse(optional.get());
    }

    private ResponseEntity<?> createAuthResponse(User user) {
        UserDetailsImpl userDetails = generateUserDetails(user);
        String accessToken = tokenUtil.generateAccessToken(userDetails);
        String refreshToken = tokenUtil.generateRefreshToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(accessToken,refreshToken,tokenUtil.getAccessExpirationTimeIn(),tokenUtil.getRefreshExpirationTimeIn()));
    }

    private Optional<User> findFirstByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    private UserDetailsImpl generateUserDetails(User userEntity) {
        return new UserDetailsImpl(userEntity);
    }

    public ResponseEntity<?> signUp(SignUpRequest signUpRequest, HttpServletRequest request) {
        Optional<User> optional = findFirstByUsername(signUpRequest.getUsername());
        if (optional.isPresent()) {
            throw new UserAlreadyExistException(signUpRequest.getPassword());
        }
        User user = User.builder()
                .fullName(signUpRequest.getFullName())
                .role(UserRole.USER) // role static test project uchun
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .balance(0L)
                .build();
        userRepository.save(user);
        return createAuthResponse(user);
    }
}
