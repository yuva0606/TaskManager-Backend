package com.yuva.TaskManager.service;

import com.yuva.TaskManager.Dto.TaskDto;
import com.yuva.TaskManager.exceptions.TaskNotFoundException;
import com.yuva.TaskManager.model.Project;
import com.yuva.TaskManager.model.Task;
import com.yuva.TaskManager.repository.TaskRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepo taskRepo;
    private final ProjectService projectService;

    public TaskService(TaskRepo taskRepo, ProjectService projectService) {
        this.projectService = projectService;
        this.taskRepo = taskRepo;
    }

    public List<TaskDto> getAllTasks() {
        List<Task> tasks = taskRepo.findAll();
        List<TaskDto> taskDtos = new ArrayList<>();

        for (Task task : tasks) {
            TaskDto taskDto = new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(), task.getStatus(), task.getDueDate(), task.getProject().getId());
            taskDtos.add(taskDto);
        }
        return taskDtos;
    }

    public List<TaskDto> getAllTasksByUserId(int userId) {
        List<Task> tasks = taskRepo.findAllByProjectUserId(userId);
        List<TaskDto> taskDtos = new ArrayList<>();

        for (Task task : tasks) {
            TaskDto taskDto = new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(), task.getStatus(), task.getDueDate(), task.getProject().getId());
            taskDtos.add(taskDto);
        }
        return taskDtos;
    }

    public Task getTaskById(int id) {
        return taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
    }
    public TaskDto getTaskDtoById(int id) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
        if (task != null) {
            return new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(), task.getStatus(), task.getDueDate(), task.getProject().getId());
        } else {
            return null;
        }
    }

    public TaskDto createTask(TaskDto taskDto) {
        Project project = projectService.getProjectById(taskDto.projectId());
        Task task = new Task(0, taskDto.title(), taskDto.description(), taskDto.status(), taskDto.priority(), project, taskDto.dueDate());
        Task savedTask = taskRepo.save(task);

        return new TaskDto(savedTask.getId(), savedTask.getTitle(), savedTask.getDescription(), savedTask.getPriority(), savedTask.getStatus(), savedTask.getDueDate(), savedTask.getProject().getId());
    }


    public Task updateTask(int id, TaskDto taskDto) {
        Task existingTask = taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
        if (existingTask != null) {
            existingTask.setTitle(taskDto.title());
            existingTask.setDescription(taskDto.description());
            existingTask.setStatus(taskDto.status());
            existingTask.setPriority(taskDto.priority());
            existingTask.setDueDate(taskDto.dueDate());
            existingTask.setProject(projectService.getProjectById(taskDto.projectId()));
            return taskRepo.save(existingTask);
        }
        return null;
    }


    public void deleteTask(int id) {
        taskRepo.deleteById(id);
    }


    public List<Task> getTasksByProjectId(int projectId) {
        return taskRepo.findByProject_Id(projectId);
    }
}
