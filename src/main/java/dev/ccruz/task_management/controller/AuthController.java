package dev.ccruz.task_management.controller;

import dev.ccruz.task_management.domain.User;
import dev.ccruz.task_management.dto.request.LoginRequest;
import dev.ccruz.task_management.dto.request.RegisterRequest;
import dev.ccruz.task_management.dto.response.AuthResponse;
import dev.ccruz.task_management.security.JwtTokenProvider;
import dev.ccruz.task_management.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = authService.register(
                request.getName(), request.getLastName(), request.getEmail(), request.getPassword());
        String token = tokenProvider.generateToken(user);
        AuthResponse response = new AuthResponse(token, user.getEmail(), user.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.getEmail(), request.getPassword());
        String token = tokenProvider.generateToken(user);
        AuthResponse response = new AuthResponse(token, user.getEmail(), user.getName());
        return ResponseEntity.ok(response);
    }
}
