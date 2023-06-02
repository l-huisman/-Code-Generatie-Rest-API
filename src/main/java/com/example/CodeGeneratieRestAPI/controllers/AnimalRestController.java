package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.models.Animal;
import com.example.CodeGeneratieRestAPI.services.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
