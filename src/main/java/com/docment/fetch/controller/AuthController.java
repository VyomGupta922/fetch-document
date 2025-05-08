package com.docment.fetch.controller;

import com.docment.fetch.commons.InvalidEmailException;
import com.docment.fetch.config.JwtService;
import com.docment.fetch.dto.response.AuthResponse;
import com.docment.fetch.dto.request.LoginRequest;
import com.docment.fetch.dto.request.RegisterRequest;
import com.docment.fetch.entity.Role;
import com.docment.fetch.entity.User;
import com.docment.fetch.repo.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.naming.InvalidNameException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+$");

    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.info("Register attempt for username: {}", request.getUsername());

            // Validate name contains only characters
            if (!NAME_PATTERN.matcher(request.getUsername()).matches()) {
                throw new InvalidNameException("Username must contain only alphabetic characters");
            }

            // Validate email format
            if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
                throw new InvalidEmailException("Invalid email format");
            }

            if (userRepo.existsByUsername(request.getUsername())) {
                logger.warn("Registration failed - username already exists: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }


            Set<Role> roles = new HashSet<>();
            for (String r : request.getRoles()) {
                try {
                    roles.add(Role.valueOf(r.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid role provided: {}", r);
                    return ResponseEntity.badRequest().body("Invalid role: " + r);
                }
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .roles(roles)
                    .build();

            userRepo.save(user);
            logger.info("User registered successfully: {}", request.getUsername());
            return ResponseEntity.ok("User registered successfully");

        } catch (InvalidNameException | InvalidEmailException e) {
            logger.error("Validation error during registration: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Login attempt for username: {}", request.getUsername());

            // Authenticate the user
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // Load user details
            User user = userRepo.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities(user.getRoles().stream()
                            .map(role -> "ROLE_" + role.name())
                            .toArray(String[]::new))
                    .build();

            // Generate token
            String token = jwtService.generateToken(userDetails);
            logger.info("Login successful for username: {}", request.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (BadCredentialsException e) {
            logger.warn("Login failed - bad credentials for username: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (UsernameNotFoundException e) {
            logger.warn("Login failed - user not found: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (DisabledException e) {
            logger.warn("Login failed - account disabled: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account disabled");
        } catch (LockedException e) {
            logger.warn("Login failed - account locked: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account locked");
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }
}