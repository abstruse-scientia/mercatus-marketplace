package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);


    boolean existsByEmail(String mail);

    User getReferenceByUserId(Long id);

    User findByUserId(Long userId);
}
