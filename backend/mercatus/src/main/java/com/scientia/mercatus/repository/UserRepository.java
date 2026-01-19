package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);

    User findByUserId(Long userId);

    boolean existsByEmail(String mail);

    User getReferenceByUserId(Long id);
}
