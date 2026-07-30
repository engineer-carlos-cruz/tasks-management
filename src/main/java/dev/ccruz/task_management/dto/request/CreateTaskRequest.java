package dev.ccruz.task_management.dto.request;

import dev.ccruz.task_management.domain.Priority;
import java.time.LocalDate;

public class CreateTaskRequest {

    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private Long projectId;
    private Long assigneeId;

    public CreateTaskRequest() {
    }

    public CreateTaskRequest(String title, String description, Priority priority, LocalDate dueDate,
                             Long projectId, Long assigneeId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
}
