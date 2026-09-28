package com.yuva.TaskManager.repository;

import com.yuva.TaskManager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Integer> {
    List<Project> findByUser_Id(int userID);

    Optional<Project> findByIdAndUser_Id(int projectId, int userId);
}
