package dev.ccruz.task_management.service;

import dev.ccruz.task_management.domain.User;
import dev.ccruz.task_management.exception.DuplicateEmailException;
import dev.ccruz.task_management.exception.UnauthorizedException;
import dev.ccruz.task_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @Test
    void registerHashesPassword() {
        when(userRepository.existsByEmail("carlos@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> {User u = inv.getArgument(0); u.setId(1L); return u;});

        User saved = authService.register("Carlos", "Cruz", "carlos@test.com", "password123");

        assertNotEquals("password123", saved.getPassword());
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("carlos@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
                () -> authService.register("Carlos", "Cruz", "carlos@test.com", "password123"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginSucceedsWithMatchingPassword() {
        User user = new User("Carlos", "Cruz", "carlos@test.com", "hashed");
        when(userRepository.findByEmail("carlos@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);

        User loggedIn = authService.login("carlos@test.com", "password123");

        assertEquals(user, loggedIn);
    }

    @Test
    void loginThrowsUnauthorizedOnInvalidCredentials() {
        when(userRepository.findByEmail("carlos@test.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class,
                () -> authService.login("carlos@test.com", "wrong-password"));
    }
}