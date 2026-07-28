package dev.ccruz.task_management.repository;

import dev.ccruz.task_management.domain.Priority;
import dev.ccruz.task_management.domain.Task;
import dev.ccruz.task_management.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssigneeId(Long assigneeId);

    List<Task> findByCreatorId(Long creatorId);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(Priority priority);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByProjectIdAndPriority(Long projectId, Priority priority);

    List<Task> findByProjectIdAndStatusAndPriority(Long projectId, TaskStatus status, Priority priority);

    List<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

    List<Task> findByAssigneeIdAndPriority(Long assigneeId, Priority priority);

    List<Task> findByAssigneeIdAndStatusAndPriority(Long assigneeId, TaskStatus status, Priority priority);

    List<Task> findByStatusAndPriority(TaskStatus status, Priority priority);
}
