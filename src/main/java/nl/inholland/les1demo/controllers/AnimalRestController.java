package nl.inholland.les1demo.controllers;

import nl.inholland.les1demo.models.Animal;
import nl.inholland.les1demo.services.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
