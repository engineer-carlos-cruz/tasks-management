package dev.ccruz.task_management.security;

import dev.ccruz.task_management.domain.User;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    public String generateToken(User user) {
        return "placeholder-jwt-token";
    }
}
