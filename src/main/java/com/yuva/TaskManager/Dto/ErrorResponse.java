package com.yuva.TaskManager.Dto;

public record ErrorResponse(
        int status,
        String message
) {}
