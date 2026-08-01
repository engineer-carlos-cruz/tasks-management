package dev.ccruz.task_management.mapper;

import dev.ccruz.task_management.domain.Project;
import dev.ccruz.task_management.dto.response.ProjectResponse;

public class ProjectMapper {

    public static ProjectResponse toResponse(Project project) {
        if (project == null) {
            return null;
        }

        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setOwnerId(project.getOwner().getId());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        return response;
    }
}
