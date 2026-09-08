package com.yuva.TaskManager.Dto;

import java.util.Date;

public record TaskDto(int id, String title, String description, String priority, String status, Date dueDate,
                      int projectId) {
}
