package com.example.CodeGeneratieRestAPI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BaseEntity {

    @Id
    @GeneratedValue
    private long id;
}
