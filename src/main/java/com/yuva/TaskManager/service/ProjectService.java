package com.yuva.TaskManager.service;

import com.yuva.TaskManager.model.Project;
import com.yuva.TaskManager.repository.ProjectRepo;
import com.yuva.TaskManager.repository.TaskRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepo projectRepo;
    private final TaskRepo taskRepo;
    
    public ProjectService(ProjectRepo projectRepo, TaskRepo taskRepo) {
        this.projectRepo = projectRepo;
        this.taskRepo = taskRepo;
    }


    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public Project createProject(Project project) {
        System.out.println(project);
        return projectRepo.save(project);
    }

    public Project updateProject(Project project) {
        return projectRepo.save(project);
    }

    public Project getProjectById(int id) {
        return projectRepo.findById(id).orElse(null);
    }

    @Transactional
    public void deleteProject(int id) {
        taskRepo.deleteByProject_Id(id);
        projectRepo.deleteById(id);
    }

}
