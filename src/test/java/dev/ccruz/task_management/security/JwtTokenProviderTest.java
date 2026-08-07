package dev.ccruz.task_management.security;

import dev.ccruz.task_management.domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(
            "testSecretKeyThatMustBeAtLeast256BitsLongForHs256Signing!", 60000);

    @Test
    void generateTokenCreatesValidToken() {
        User user = new User("Carlos", "Cruz", "carlos@test.com", "plain");
        user.setId(1L);

        String token = tokenProvider.generateToken(user);

        assertNotNull(token);
        assertTrue(tokenProvider.isValid(token));
        assertEquals(1L, tokenProvider.getUserIdFromToken(token));
        assertEquals("carlos@test.com", tokenProvider.getEmailFromToken(token));
    }

    @Test
    void rejectsTokenSignedWithDifferentSecret() {
        JwtTokenProvider other = new JwtTokenProvider(
                "anotherSecretKeyThatMustBeAtLeast256BitsLongForHs256Signing!", 60000);
        User user = new User("Carlos", "Cruz", "carlos@test.com", "plain");
        user.setId(1L);

        String token = other.generateToken(user);

        assertFalse(tokenProvider.isValid(token));
    }

    @Test
    void rejectsExpiredToken() throws InterruptedException {
        JwtTokenProvider shortLived = new JwtTokenProvider(
                "testSecretKeyThatMustBeAtLeast256BitsLongForHs256Signing!", 1);
        User user = new User("Carlos", "Cruz", "carlos@test.com", "plain");
        user.setId(1L);

        String token = shortLived.generateToken(user);
        Thread.sleep(5);

        assertFalse(shortLived.isValid(token));
    }
}