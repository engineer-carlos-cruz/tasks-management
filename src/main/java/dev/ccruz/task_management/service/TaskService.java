package dev.ccruz.task_management.service;

import dev.ccruz.task_management.domain.Priority;
import dev.ccruz.task_management.domain.Project;
import dev.ccruz.task_management.domain.Task;
import dev.ccruz.task_management.domain.TaskStatus;
import dev.ccruz.task_management.domain.User;
import dev.ccruz.task_management.exception.ForbiddenException;
import dev.ccruz.task_management.exception.ResourceNotFoundException;
import dev.ccruz.task_management.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, ProjectService projectService,
                       UserService userService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userService = userService;
    }

    public List<Task> findByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public List<Task> findByAssigneeId(Long assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId);
    }

    public List<Task> findByCreatorId(Long creatorId) {
        return taskRepository.findByCreatorId(creatorId);
    }

    public List<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> findByPriority(Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    public List<Task> findFiltered(Long projectId, TaskStatus status, Priority priority) {
        if (projectId != null && status != null && priority != null) {
            return taskRepository.findByProjectIdAndStatusAndPriority(projectId, status, priority);
        }
        if (projectId != null && status != null) {
            return taskRepository.findByProjectIdAndStatus(projectId, status);
        }
        if (projectId != null && priority != null) {
            return taskRepository.findByProjectIdAndPriority(projectId, priority);
        }
        if (projectId != null) {
            return taskRepository.findByProjectId(projectId);
        }
        if (status != null && priority != null) {
            return taskRepository.findByStatusAndPriority(status, priority);
        }
        if (status != null) {
            return taskRepository.findByStatus(status);
        }
        if (priority != null) {
            return taskRepository.findByPriority(priority);
        }
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }

    public Task findByIdAndUser(Long id, User user) {
        Task task = findById(id);
        Project project = task.getProject();
        boolean isOwner = project.getOwner().getId().equals(user.getId());
        boolean isCreator = task.getCreator().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee() != null
                && task.getAssignee().getId().equals(user.getId());
        if (!isOwner && !isCreator && !isAssignee) {
            throw new ForbiddenException("You do not have access to this task");
        }
        return task;
    }

    public Task create(String title, String description, Priority priority, LocalDate dueDate,
                       Long projectId, User creator, Long assigneeId) {
        Project project = projectService.findById(projectId);
        if (!project.getOwner().getId().equals(creator.getId())) {
            throw new ForbiddenException("You do not have access to this project");
        }

        User assignee = null;
        if (assigneeId != null) {
            assignee = userService.findById(assigneeId);
        }

        Task task = new Task(title, description, priority, dueDate, project, creator, assignee);
        return taskRepository.save(task);
    }

    public Task update(Long id, String title, String description, TaskStatus status,
                       Priority priority, LocalDate dueDate, Long assigneeId, User currentUser) {
        Task task = findById(id);
        Project project = task.getProject();
        boolean isOwner = project.getOwner().getId().equals(currentUser.getId());
        boolean isCreator = task.getCreator().getId().equals(currentUser.getId());
        if (!isOwner && !isCreator) {
            throw new ForbiddenException("You do not have permission to update this task");
        }

        if (title != null) {
            task.setTitle(title);
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (status != null) {
            task.setStatus(status);
        }
        if (priority != null) {
            task.setPriority(priority);
        }
        if (dueDate != null) {
            task.setDueDate(dueDate);
        }
        if (assigneeId != null) {
            User assignee = userService.findById(assigneeId);
            task.setAssignee(assignee);
        }

        return taskRepository.save(task);
    }

    public void delete(Long id, User currentUser) {
        Task task = findById(id);
        Project project = task.getProject();
        boolean isOwner = project.getOwner().getId().equals(currentUser.getId());
        if (!isOwner) {
            throw new ForbiddenException("Only the project owner can delete tasks");
        }
        taskRepository.delete(task);
    }
}
