package com.yuva.TaskManager.controller;

import com.yuva.TaskManager.Dto.ProjectDto;
import com.yuva.TaskManager.Dto.TaskDto;
import com.yuva.TaskManager.model.Project;
import com.yuva.TaskManager.model.Task;
import com.yuva.TaskManager.model.UserDetailsImpl;
import com.yuva.TaskManager.service.ProjectService;
import com.yuva.TaskManager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<List<ProjectDto>> getProjects(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<ProjectDto> projects = projectService.getAllProjectsByUserId(userDetails.getUser().getId());
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto, Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Project project = new Project(projectDto.id(), projectDto.name(), userDetails.getUser());

        ProjectDto createdProject = projectService.createProject(project);
        return ResponseEntity.ok(createdProject);
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable int id, @Valid @RequestBody ProjectDto projectDto, Authentication authentication) {
        if (!checkProjectOwnership(id, authentication)) {
            return ResponseEntity.status(403).build();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Project project = new Project(projectDto.id(), projectDto.name(), userDetails.getUser());
        ProjectDto updatedProject = projectService.updateProject(project);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable int id, Authentication authentication) {
        if (!checkProjectOwnership(id, authentication)) {
            return ResponseEntity.status(403).build();
        }
        ProjectDto projectDto = projectService.getProjectDtoById(id);
        return ResponseEntity.ok(projectDto);
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable int id, Authentication authentication) {
        if (!checkProjectOwnership(id, authentication)) {
            return ResponseEntity.status(403).build();
        }

        System.out.println("to delete project with id: " + id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/{id}/tasks")
    public ResponseEntity<List<Task>> getTasksByProjectId(@PathVariable int id, Authentication authentication) {
        if (!checkProjectOwnership(id, authentication)) {
            return ResponseEntity.status(403).build();
        }

        List<Task> tasks = taskService.getTasksByProjectId(id);
        return ResponseEntity.ok(tasks);
    }

    public boolean checkProjectOwnership(int projectId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        int userId = userDetails.getUser().getId();

        Project project = projectService.getProjectByIdAndUserId(projectId, userId); // check if the user owns the project

        return project.getUser().getId() == userId;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasks(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<TaskDto> tasks = taskService.getAllTasksByUserId(userDetails.getUser().getId());
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto task, Authentication authentication) {
        if (!checkProjectOwnership(task.projectId(), authentication)) {
            return ResponseEntity.status(403).build();
        }


        TaskDto createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    public boolean checkTaskOwnership(int taskId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Task task = taskService.getTaskById(taskId);
        return task.getProject().getUser().getId() == userDetails.getUser().getId();
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> updateTask(@Valid @RequestBody TaskDto task, @PathVariable int id, Authentication authentication) {

        if (!checkTaskOwnership(id, authentication)) {
            return ResponseEntity.status(403).build();
        }

        if (!checkProjectOwnership(task.projectId(), authentication)) {
            return ResponseEntity.status(403).build();
        }

        System.out.println("update task" + task);
        Task updatedTask = taskService.updateTask(id, task);
        TaskDto taskDto = new TaskDto(updatedTask.getId(), updatedTask.getTitle(), updatedTask.getDescription(), updatedTask.getPriority(), updatedTask.getStatus(), updatedTask.getDueDate(), updatedTask.getProject().getId());
        return ResponseEntity.ok(taskDto);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable int id, Authentication authentication) {

        if (!checkTaskOwnership(id, authentication)) {
            return ResponseEntity.status(403).build();
        }
        TaskDto task = taskService.getTaskDtoById(id);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id, Authentication authentication) {

        if (!checkTaskOwnership(id, authentication)) {
            return ResponseEntity.status(403).build();
        }
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
