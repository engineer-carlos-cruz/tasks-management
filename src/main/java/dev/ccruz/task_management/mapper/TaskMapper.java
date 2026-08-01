package dev.ccruz.task_management.mapper;

import dev.ccruz.task_management.domain.Task;
import dev.ccruz.task_management.dto.response.TaskResponse;

public class TaskMapper {

    public static TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }

        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setProjectId(task.getProject().getId());
        response.setCreatorId(task.getCreator().getId());
        response.setAssigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null);
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }
}
