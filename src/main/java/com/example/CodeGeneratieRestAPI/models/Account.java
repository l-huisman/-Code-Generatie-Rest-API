package com.example.CodeGeneratieRestAPI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data

@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"accounts\"")
public class Account extends BaseEntity {

}
