package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsernameAndPassword(String username, String password);
}
