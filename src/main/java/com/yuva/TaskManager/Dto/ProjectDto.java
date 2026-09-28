package com.yuva.TaskManager.Dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectDto(
        int id,

        @NotBlank
        String name
) {}
