package com.example.CodeGeneratieRestAPI.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Animal {
    private long id;
    private String name;
    private String species;
}
