package dev.ccruz.task_management.controller;

import dev.ccruz.task_management.domain.User;
import dev.ccruz.task_management.dto.request.UpdateUserRequest;
import dev.ccruz.task_management.dto.response.UserResponse;
import dev.ccruz.task_management.mapper.UserMapper;
import dev.ccruz.task_management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestAttribute("user") User currentUser) {
        return ResponseEntity.ok(UserMapper.toResponse(currentUser));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @RequestAttribute("user") User currentUser,
            @RequestBody UpdateUserRequest request) {
        User updated = userService.updateUser(
                currentUser.getId(), request.getName(), request.getLastName());
        return ResponseEntity.ok(UserMapper.toResponse(updated));
    }
}
