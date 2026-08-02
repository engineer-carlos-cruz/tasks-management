package dev.ccruz.task_management.controller;

import dev.ccruz.task_management.domain.Project;
import dev.ccruz.task_management.domain.User;
import dev.ccruz.task_management.dto.request.CreateProjectRequest;
import dev.ccruz.task_management.dto.request.UpdateProjectRequest;
import dev.ccruz.task_management.dto.response.ProjectResponse;
import dev.ccruz.task_management.mapper.ProjectMapper;
import dev.ccruz.task_management.service.ProjectService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listProjects(
            @RequestAttribute("user") User currentUser) {
        List<Project> projects = projectService.findByOwnerId(currentUser.getId());
        List<ProjectResponse> response = projects.stream()
                .map(ProjectMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @RequestAttribute("user") User currentUser,
            @RequestBody CreateProjectRequest request) {
        Project project = projectService.create(
                request.getName(), request.getDescription(), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProjectMapper.toResponse(project));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(
            @PathVariable Long id,
            @RequestAttribute("user") User currentUser) {
        Project project = projectService.findByIdAndUser(id, currentUser);
        return ResponseEntity.ok(ProjectMapper.toResponse(project));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @RequestAttribute("user") User currentUser,
            @RequestBody UpdateProjectRequest request) {
        Project project = projectService.update(
                id, request.getName(), request.getDescription(), currentUser);
        return ResponseEntity.ok(ProjectMapper.toResponse(project));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            @RequestAttribute("user") User currentUser) {
        projectService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
