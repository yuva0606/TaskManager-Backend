package com.yuva.TaskManager.repository;

import com.yuva.TaskManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByRefreshToken(String refreshToken);
    Optional<User> findByGoogleSubject(String googleSubject);
}
