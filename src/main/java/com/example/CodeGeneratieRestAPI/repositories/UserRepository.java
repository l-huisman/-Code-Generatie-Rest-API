package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.User;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByUsername(String username);    
    User findByUsernameAndPassword(String username, String password);
    Boolean existsByUsername(String username);
}

