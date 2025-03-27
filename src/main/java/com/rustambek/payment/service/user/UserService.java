package com.rustambek.payment.service.user;

import com.rustambek.payment.config.TokenUtil;
import com.rustambek.payment.config.UserDetailsImpl;
import com.rustambek.payment.dto.auth.AuthResponse;
import com.rustambek.payment.dto.auth.SignInRequest;
import com.rustambek.payment.dto.auth.SignUpRequest;
import com.rustambek.payment.exception.RecordForbiddenException;
import com.rustambek.payment.exception.RecordNotFoundException;
import com.rustambek.payment.exception.UserAlreadyExistException;
import com.rustambek.payment.mapper.users.UserMapper;
import com.rustambek.payment.model.user.User;
import com.rustambek.payment.model.user.UserRole;
import com.rustambek.payment.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;
    private final UserMapper userMapper;

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
        checkExistsUsername(signUpRequest.getUsername());
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

    private void checkExistsUsername(String username) {
        Optional<User> optional = findFirstByUsername(username);
        if (optional.isPresent()) {
            throw new UserAlreadyExistException(username+" already exists");
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetail = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetail.getId()).get();
    }

    public void saveViaViod(User user) {
        userRepository.save(user);
    }

    public ResponseEntity<?> create(SignUpRequest requestDto, HttpServletRequest request) {
        checkExistsUsername(requestDto.getUsername());
        User user = User.builder()
                .fullName(requestDto.getFullName())
                .role(UserRole.USER) // role static test project uchun
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .balance(requestDto.getBalance())
                .build();
        User savedModel = userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(savedModel));
    }

    public ResponseEntity<?> getById(UUID id) {
        User model = findById(id);
        return ResponseEntity.ok(userMapper.toDto(model));
    }

    private User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("User with id: " + id + " not found"));
    }

    public ResponseEntity<?> update(UUID id, @Valid SignUpRequest requestDto) {
        User model = findById(id);
        if (requestDto.getFullName() != null)
            model.setFullName(requestDto.getFullName());
        if (requestDto.getUsername() != null)
            model.setUsername(requestDto.getUsername());
        if (requestDto.getPassword() != null)
            model.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        if (requestDto.getBalance() != null)
            model.setBalance(requestDto.getBalance());
        userRepository.save(model);
        return ResponseEntity.ok(userMapper.toDto(model));
    }

    public ResponseEntity<?> pageable(Pageable pageable) {
        Page<User> all = userRepository.findAll(pageable);
        return ResponseEntity.ok(userMapper.toDtoPage(all));
    }

    public ResponseEntity<?> delete(UUID id) {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
        }else {
            throw new RecordNotFoundException("User with id: " + id + " not found");
        }
        return ResponseEntity.ok("SUCCESS");

    }
}
