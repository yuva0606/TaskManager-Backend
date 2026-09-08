package com.yuva.TaskManager.repository;

import com.yuva.TaskManager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Integer> {

    List<Task> findByProject_Id(int projectId);

    void deleteByProject_Id(int projectId);
}
