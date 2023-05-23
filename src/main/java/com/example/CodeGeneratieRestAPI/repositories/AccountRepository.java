package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

}
