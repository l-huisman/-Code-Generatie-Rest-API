package com.example.CodeGeneratieRestAPI.controllers;


import com.example.CodeGeneratieRestAPI.models.Animal;
import com.example.CodeGeneratieRestAPI.services.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/animals")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity getAll() {
        return ResponseEntity.ok(animalService.getAll());
    }

    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity add(@RequestBody Animal animal) {
        return ResponseEntity.ok(animalService.add(animal));
    }


}
