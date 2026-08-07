package dev.ccruz.task_management.service;

import dev.ccruz.task_management.domain.User;
import dev.ccruz.task_management.exception.DuplicateEmailException;
import dev.ccruz.task_management.exception.UnauthorizedException;
import dev.ccruz.task_management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String name, String lastName, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(name, lastName, email, encodedPassword);
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.isEnabled()
                        && passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
    }
}