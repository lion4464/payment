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
import com.rustambek.payment.enums.user.UserRole;
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

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceIml implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;
    private final UserMapper userMapper;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RecordForbiddenException("User with username: " + username + " not found"));
    }

    public ResponseEntity<?> signIn(SignInRequest request) {
        User user = authenticateUser(request.getUsername(), request.getPassword());
        return createAuthResponse(user);
    }



    private Optional<User> findFirstByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    private UserDetailsImpl generateUserDetails(User userEntity) {
        return new UserDetailsImpl(userEntity);
    }

    public ResponseEntity<?> signUp(SignUpRequest request) {
        validateSignUpRequest(request);
        User user = createUserFromRequest(request);
        userRepository.save(user);
        return createAuthResponse(user);
    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetail = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetail.getId()).get();
    }

    public void saveViaViod(User user) {
        userRepository.save(user);
    }

    public ResponseEntity<?> getById(UUID id) {
        User model = findById(id);
        return ResponseEntity.ok(userMapper.toDto(model));
    }

    public ResponseEntity<?> create(SignUpRequest requestDto) {
        checkExistsUsername(requestDto.getUsername());
        User user = createUserFromRequest(requestDto);
        User savedModel = userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(savedModel));
    }


    public ResponseEntity<?> update(UUID id, SignUpRequest requestDto) {
        User model = findById(id);
        validateUpdate(requestDto,model);
        userRepository.save(model);
        return ResponseEntity.ok(userMapper.toDto(model));
    }


    public ResponseEntity<?> pageable(Pageable pageable) {
        Page<User> all = userRepository.findAll(pageable);
        return ResponseEntity.ok(userMapper.toDtoPage(all));
    }

    public ResponseEntity<?> delete(UUID id) {
        existUserById(id);
        userRepository.deleteById(id);
        return ResponseEntity.ok("SUCCESS");

    }

    private ResponseEntity<?> createAuthResponse(User user) {
        UserDetailsImpl userDetails = generateUserDetails(user);
        String accessToken = tokenUtil.generateAccessToken(userDetails);
        String refreshToken = tokenUtil.generateRefreshToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(accessToken,refreshToken,tokenUtil.getAccessExpirationTimeIn(),tokenUtil.getRefreshExpirationTimeIn()));
    }

    private Boolean existUserById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RecordNotFoundException("User with id: " + id + " not found");
        }
        return true;
    }
    private void validateSignUpRequest(SignUpRequest request) {
        checkExistsUsername(request.getUsername());
    }

    private User createUserFromRequest(SignUpRequest request) {
        return User.builder()
                .fullName(request.getFullName())
                .role(UserRole.USER)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .balance(0L)
                .build();
    }


    private void checkExistsUsername(String username) {
        Optional<User> optional = findFirstByUsername(username);
        if (optional.isPresent()) {
            throw new UserAlreadyExistException(username+" already exists");
        }
    }



    private void validateUpdate(SignUpRequest requestDto, User model) {
        if (requestDto.getFullName() != null)
            model.setFullName(requestDto.getFullName());
        if (requestDto.getUsername() != null)
            model.setUsername(requestDto.getUsername());
        if (requestDto.getPassword() != null)
            model.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        if (requestDto.getBalance() != null)
            model.setBalance(requestDto.getBalance());
    }


    private User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("User with id: " + id + " not found"));
    }

    private User authenticateUser(String username, String password) {
        User user = findFirstByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return user;
    }
}
