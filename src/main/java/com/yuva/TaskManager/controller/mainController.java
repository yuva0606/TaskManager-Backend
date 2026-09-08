package com.yuva.TaskManager.controller;

import com.yuva.TaskManager.Dto.TaskDto;
import com.yuva.TaskManager.model.Project;
import com.yuva.TaskManager.model.Task;
import com.yuva.TaskManager.service.ProjectService;
import com.yuva.TaskManager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
public class mainController {
    private TaskService taskService;
    private ProjectService projectService;

    public mainController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        return ResponseEntity.ok(createdProject);
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<Project> updateProject(@RequestBody Project project) {
        Project updatedProject = projectService.updateProject(project);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable int id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable int id) {
        System.out.println("to delete project with id: " + id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/{id}/tasks")
    public ResponseEntity<List<Task>> getTasksByProjectId(@PathVariable int id) {
        List<Task> tasks = taskService.getTasksByProjectId(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto task) {
        TaskDto createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> updateTask(@RequestBody TaskDto task) {
        Task updatedTask = taskService.updateTask(task);
        TaskDto taskDto = new TaskDto(updatedTask.getId(), updatedTask.getTitle(), updatedTask.getDescription(), updatedTask.getPriority(), updatedTask.getStatus(), updatedTask.getDueDate(), updatedTask.getProject().getId());
        return ResponseEntity.ok(taskDto);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable int id) {
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
