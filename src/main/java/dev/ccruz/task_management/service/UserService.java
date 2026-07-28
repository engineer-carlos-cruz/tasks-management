package dev.ccruz.task_management.service;

import dev.ccruz.task_management.domain.User;
import dev.ccruz.task_management.exception.ResourceNotFoundException;
import dev.ccruz.task_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User updateUser(Long id, String name, String lastName) {
        User user = findById(id);
        if (name != null) {
            user.setName(name);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        return userRepository.save(user);
    }
}
