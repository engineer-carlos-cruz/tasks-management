package dev.ccruz.task_management.service;

import dev.ccruz.task_management.domain.Project;
import dev.ccruz.task_management.domain.User;
import dev.ccruz.task_management.exception.ForbiddenException;
import dev.ccruz.task_management.exception.ResourceNotFoundException;
import dev.ccruz.task_management.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findByOwnerId(Long ownerId) {
        return projectRepository.findByOwnerId(ownerId);
    }

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    public Project findByIdAndUser(Long id, User user) {
        Project project = findById(id);
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this project");
        }
        return project;
    }

    public Project create(String name, String description, User owner) {
        Project project = new Project(name, description, owner);
        return projectRepository.save(project);
    }

    public Project update(Long id, String name, String description, User currentUser) {
        Project project = findByIdAndUser(id, currentUser);
        if (name != null) {
            project.setName(name);
        }
        if (description != null) {
            project.setDescription(description);
        }
        return projectRepository.save(project);
    }

    public void delete(Long id, User currentUser) {
        Project project = findByIdAndUser(id, currentUser);
        projectRepository.delete(project);
    }
}
