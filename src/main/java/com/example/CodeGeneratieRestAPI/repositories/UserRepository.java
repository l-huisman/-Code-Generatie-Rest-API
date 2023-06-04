package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    User findByUsernameAndPassword(String username, String password);

    Boolean existsByUsername(String username);
}

