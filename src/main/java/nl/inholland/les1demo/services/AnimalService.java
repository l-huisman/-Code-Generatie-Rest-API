package nl.inholland.les1demo.services;

import nl.inholland.les1demo.models.Animal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnimalService {

    private List<Animal> animals = new ArrayList<>();

    public AnimalService() {
        animals.add(new Animal(1, "Harambe", "Gorilla"));
        animals.add(new Animal(2, "Grand Master Oogway", "Tortoise"));
        animals.add(new Animal(3, "Simba", "Lion"));
    }

    public List<Animal> getAll() {
        return animals;
    }

    public Animal add(Animal a) {
        animals.add(a);
        return a;
    }
}
