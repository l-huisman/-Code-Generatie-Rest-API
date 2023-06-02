package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.models.Animal;
import com.example.CodeGeneratieRestAPI.services.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"restanimals", "restanimals/"})

public class AnimalRestController {

    @Autowired
    private AnimalService animalService;

    @GetMapping
    public List<Animal> getAll() {
        return animalService.getAll();
    }

    @PostMapping
    public Animal add(@RequestBody Animal animal) {
        return animalService.add(animal);
    }

}
