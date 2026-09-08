package com.yuva.TaskManager.repository;

import com.yuva.TaskManager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Integer> {
}
