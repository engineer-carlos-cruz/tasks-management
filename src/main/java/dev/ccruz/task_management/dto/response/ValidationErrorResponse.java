package dev.ccruz.task_management.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class ValidationErrorResponse extends ErrorResponse {

    private List<ValidationErrorDetail> errors;

    public ValidationErrorResponse() {
    }

    public ValidationErrorResponse(String error, String message, int status,
                                   LocalDateTime timestamp, String path,
                                   List<ValidationErrorDetail> errors) {
        super(error, message, status, timestamp, path);
        this.errors = errors;
    }

    public List<ValidationErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationErrorDetail> errors) {
        this.errors = errors;
    }
}
