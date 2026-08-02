package dev.ccruz.task_management.controller;

import dev.ccruz.task_management.domain.Priority;
import dev.ccruz.task_management.domain.Task;
import dev.ccruz.task_management.domain.TaskStatus;
import dev.ccruz.task_management.domain.User;
import dev.ccruz.task_management.dto.request.CreateTaskRequest;
import dev.ccruz.task_management.dto.request.UpdateTaskRequest;
import dev.ccruz.task_management.dto.response.TaskResponse;
import dev.ccruz.task_management.mapper.TaskMapper;
import dev.ccruz.task_management.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> listTasks(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority) {
        List<Task> tasks = taskService.findFiltered(projectId, status, priority);
        List<TaskResponse> response = tasks.stream()
                .map(TaskMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestAttribute("user") User currentUser,
            @RequestBody CreateTaskRequest request) {
        Task task = taskService.create(
                request.getTitle(), request.getDescription(), request.getPriority(),
                request.getDueDate(), request.getProjectId(), currentUser,
                request.getAssigneeId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskMapper.toResponse(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long id,
            @RequestAttribute("user") User currentUser) {
        Task task = taskService.findByIdAndUser(id, currentUser);
        return ResponseEntity.ok(TaskMapper.toResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestAttribute("user") User currentUser,
            @RequestBody UpdateTaskRequest request) {
        Task task = taskService.update(
                id, request.getTitle(), request.getDescription(), request.getStatus(),
                request.getPriority(), request.getDueDate(), request.getAssigneeId(),
                currentUser);
        return ResponseEntity.ok(TaskMapper.toResponse(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @RequestAttribute("user") User currentUser) {
        taskService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
