package com.yuva.TaskManager.Dto;

import java.util.Date;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TaskDto(
        int id,

        @NotBlank
        String title,

        String description,

        @NotBlank
        String priority,

        @NotBlank
        String status,

        Date dueDate,

        @Min(1)
        int projectId
) {
}
